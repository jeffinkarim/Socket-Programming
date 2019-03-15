

import sys
import os
import threading
import struct
import socket
import getopt


argv = sys.argv
argc = len( sys.argv )

def usage(script_name):
   
    print( 'Usage for Server: ' + script_name + ' -l <listen port>  or Usage for Client ' + ' -l <listen port> script_name [-s] [connect server address]\
                -p <connect server port>' )


class MessengerWithFiles:

    def __init__(self, port):                 #initalize
        self.port = port
        self.threads = []
        self.listenersocket = ''
        self.serverhost = ''
        self.serverport = ''
        self.messagesocket   = ''

    def userinterface(self):			#get user input and give options for the user
     
        while True:                         # countinously reads user input, til user Xits 
            print("Enter an option ('m', 'f', 'x'):  ")
            print("(M)essage (send) ")
            print("(F)ile (request) ")
            print("e(X)it ")
            str = sys.stdin.readline().strip()
            
            if str == 'm':                   # sends message
                print("Enter your message:")
                line = sys.stdin.readline()
                self.sendmsg(line)

            elif str == 'f':                    # gets the file
                print("Which file do you want?")
                filename = sys.stdin.readline().strip()
                self.getfile(filename)

            elif str == 'x':         # exits and closes the sockets
                break
        print("closing your sockets...goodbye")
        self.closeSocks()

    def listener(self, port):                 # socket to listen on the port
        
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            s.bind(('localhost', int(port)))
            s.listen(5)
        except:
            self.closeSocks()
        return s

    def requestconnection(self, host, port):          # open socket and call connect on it
        											
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect( (host, int(port)) )
        return s

    def acceptconnection(self):                       # call accept() on the listener
        
        sock, addr = self.listenersocket.accept()		# returns messagesocket to server
        print("Connection accepted")					#accept returns (socket, (host, port))
        return (sock, addr)

    def run(self):		# start the threads for both the messages and file requests and starts user interface
      
        msgthread = threading.Thread(target=self.getmessages) ## makes thread to get messages
        msgthread.start()
        self.threads.append(msgthread)                             
        filethread = threading.Thread(target=self.filelistener)   ## makes thread to listen for file requests
        filethread.start()
        self.threads.append(filethread)
        self.userinterface()                    ## get the user input, main thread


    def sendmsg(self, text):
        self.messagesocket.send(text.encode())

    def getmessages(self):
   
        message = self.messagesocket.recv(1024)
        while message:                              ## while there is message we print it, then close
            print(message.decode(), end = '')
            message = self.messagesocket.recv(1024)
        self.closeSocks()


    def filelistener(self):
        
        print("Opening file server...")
        while True:
            sock, addr = self.acceptconnection()       ## we get the connection
            filethread = threading.Thread(target=MessengerWithFiles.requesthandler, args=(sock,))
            filethread.start()         ## start a thread that calls handle_request to get the filename and send file
            self.threads.append(filethread)


    def requesthandler(sock):		# get the filename, check if file exists and send back the file size
       
        msg = sock.recv(1024)
    
        filename= msg.decode()
      
        print("Received file")
        file_stat= os.stat(filename)
        if file_stat.st_size:
            print("Found file")
            file= open( filename, 'rb' )
            MessengerWithFiles.sendfile(sock, file_stat.st_size, file)
        else:
            print("File not found")
          
        sock.close()


    def sendfile(sock, file_size, file):	# send the file
       
        print('File size is ' + str(file_size))
        file_size_bytes= struct.pack('!L', file_size)
  
        sock.send(file_size_bytes)
 
        while True:
            bytes= file.read(1024)
            if bytes:
                sock.send(bytes)
            else:
                break
        file.close()


    def getfile(self, filename):
       
        t = threading.Thread(target=self.requestfile, args=(filename,))
        self.threads.append(t)
        t.start()

    def requestfile(self, filename):
        												# connects to fileserver and receives the file
        try:
            filesocket = self.requestconnection(self.serverhost, self.serverport)
            if filesocket:
                print("filesocket opened")
            filesocket.send(filename.encode())
            file_size_bytes = filesocket.recv(4)
            if file_size_bytes:
                file_size = struct.unpack('!L', file_size_bytes[:4])[0]
                if file_size:
                    MessengerWithFiles.receivefile(filesocket, filename)
                else:
                    print('File not received 1')
            else:
                print('File not received 2')
        except:
            print("Could not open connection to file server.")
        finally:
            if filesocket:
                filesocket.close()


   
    def receivefile(sock, filename):
        
        f = open(filename, 'wb')
        while True:
            bytes = sock.recv(1024)
            if bytes:
                f.write(bytes)
            else:
                break
        f.close()

    

    def closeSocks(self):
        self.messagesocket.close()
        self.listenersocket.close()
        os._exit(0)


class Server(MessengerWithFiles):   #makes use of MessengerWithFiles methods to intialize the server
   
    def __init__(self, listen_port):
        super().__init__(listen_port)
        self.listenersocket = self.listener(self.port)
        self.messagesocket, addr = self.acceptconnection()
        self.serverhost = addr[0]
        filerequestport = self.messagesocket.recv(4).decode() # recieves the port it should listen on
        self.serverport = filerequestport


class Client(MessengerWithFiles):       #initalize the Client
    def __init__(self, listen_port, host, port):
        super().__init__(listen_port)
        self.serverhost = host
        self.serverport = port
        self.messagesocket = self.requestconnection(self.serverhost, self.serverport)
        print("Connection accepted. Sending listen port " + listen_port)
        self.messagesocket.send(listen_port.encode())   # sends the listening port that server will recv
        self.listenersocket = self.listener(listen_port)


def options(argv, argc):
    serverboolean = True
    listen_port = ''
    host = 'localhost'
    port = ''


    options, args = getopt.getopt(argv[1:], "l:s:p:")

    for (opt, val) in options:
        if opt == '-l':
            listen_port = val
            serverboolean = True
            

        if opt == '-s':
            host = val
            serverboolean = False
            

        if opt == '-p':
            port = val
            serverboolean = False
            

    return [serverboolean, listen_port, host, port]


def main ():
    arguments = options(argv, len(argv))
    if arguments[0]:                # if serverboolean is still true then its a server, else client
        start = Server(arguments[1])
    else:
        start = Client(arguments[1], arguments[2], arguments[3])

    start.run()         

main()