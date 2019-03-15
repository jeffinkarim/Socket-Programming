/* myloggerd.c
 * Source file for thread-lab
 * Creates a server to log messages sent from various connections
 * in real time.
 *
 * Student: Jeffin Karimkuttyil
 */
 
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdlib.h>
#include <pthread.h>
#include "message-lib.h"

// forward declarations
int usage( char name[] );
// a function to be executed by each thread
void * recv_log_msgs( void * arg );

// globals
int log_fd; // opened by main() but accessible by each thread


int clientfd;

int read_size;

char client_message[4000];



// int socketfd;

int error_msg( char * msg )
{
	printf( "%s\n", msg );
	return -1;
}

void * recv_log_msgs( void * arg ){
	
	// loops to receive messages from a connection;
	// when read_msg returns zero, terminate the loop
	// and close the connection
	
	int clientfd = *((int *)arg);

	// while((read_size = recv(clientfd, client_message, 4000, 0)) > 0) {
	// 	write(log_fd, client_message, strlen(client_message));
	// }

	while(read_msg(clientfd, client_message, 4000) > 0) {
		write(log_fd, client_message, strlen(client_message));
	}

	// if(read_size == 0) {
	// 	puts("connection terminated");
	// }

	// else if(read_size == -1) {
	// 	return error_msg("connection fail");
	// }


	close(clientfd);



	return NULL;
}

int usage( char name[] ){
	printf( "Usage:\n" );
	printf( "\t%s <log-file-name> <UDS path>\n", name );
	return 1;
}

int main( int argc, char * argv[] )
{
	if ( argc != 3 )
		return usage( argv[0] );
		
	// open the log file for appending
	
	// permit message connections
			
	// loop to wait for connection requests;
	// as each connection is accepted,
	// launch a new thread that calls
	// recv_log_msgs(), which receives
	// messages and writes them to the log file			
	// when accept_next_connection returns -1, terminate the loop
	
	// close the listener
	
	// close the log file



	log_fd = open(argv[1], O_RDWR | O_APPEND | O_CREAT);

	if(log_fd == -1) {
		error_msg("No such file");
		return error_msg(argv[1]);
	}

	int listener = permit_connections(argv[2]); 
		if(listener == -1) {
			return -1;
		}

	// int connection = accept_next_connection(listener);
	// if(connection == -1) {
	// 	return -1;
	// }

	// int connection2 = accept_next_connection(listener);
	// if(connection2 == -1) {
	// 	return -1;
	// }

	// int connection3 = accept_next_connection(listener);
	// if(connection3 == -1) {
	// 	return -1;
	// }



	// int connection = accept_next_connection(listener);
	// if(connection == -1) {
	// 	return -1;
	// }

	

	pthread_t client_thread;

	while(1) {
		printf("Waiting for a connection on path %s..\n", argv[2]);

		printf("trying to connect\n");
		clientfd = accept_next_connection(listener);
		printf("connected\n");
		if(clientfd == -1) {
			close_listener(listener);
			close(log_fd);
			break;
		}

		pthread_create(&client_thread, NULL, recv_log_msgs, &clientfd);
	//	pthread_create(&client_thread, NULL, recv_log_msgs, &clientfd);
	}


	
	
	// close message connection
	
	// close_connection( connection );
	// close_connection(connection2);
	// close_connection(connection3);
	
	// close the listener
	close_listener( listener );

close(log_fd);
	
	
	return 0;
}
