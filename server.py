def broadcast_message(connection, message_json):
    '''Send a message to all client sockets connected to the server...ALL JSON ARE ENCODED'''
    for client_socket in connection.client_sockets:
        client_socket.send(message_json)


def recieve_message(connection, client_socket):
    '''Recive an incoming message from a client'''
    while True:
        #Get a message_json from a client
        try:
            message_json = client_socket.recv(connection.bytesize)
            process_message(connection, message_json, client_socket)
        except:
            break
  

def self_broadcast(connection):
    '''Broadcast a special admin message to all clients'''
    #Create a message packet
    message_packet = create_message("MESSAGE", "Admin (broadcast)", input_entry.get(), light_green)
    message_json = json.dumps(message_packet)
    broadcast_message(connection, message_json.encode(connection.encoder))

    #Clear the input entry
    input_entry.delete(0, END)
        broadcast_message(f"{client_name} has joined the chat!".encode(ENCODER))

        #Now that a new client has connected, start a thread
        recieve_thread = threading.Thread(target=recieve_message, args=(client_socket,))
        recieve_thread.start()

        
#Start the server
print("Server is listening for incoming connections...\n")
connect_client()
