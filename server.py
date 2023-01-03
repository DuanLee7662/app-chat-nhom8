#Server side Advanced GUI chat room (admin)
import tkinter, socket, threading, json
from tkinter import DISABLED, VERTICAL, END, NORMAL

#Define window
root = tkinter.Tk()
root.title("Chat Server")
root.iconbitmap("message_icon.ico")
root.geometry('600x600')
root.resizable(0,0)

#Define fonts and colors
my_font = ('SimSun', 14)
black = "#010101"
light_green = "#1fc742"
root.config(bg=black)

#Create a Connection class to hold our server socket
class Connection():
    '''A class to store a connection - a server socket and pertinent information'''
    def __init__(self):
        self.host_ip = socket.gethostbyname(socket.gethostname())
        self.encoder = 'utf-8'
        self.bytesize = 1024

        self.client_sockets = []
        self.client_ips = []
        self.banned_ips = []


#Define Functions
def start_server(connection):
    '''Start the server on a given port number'''
    #Get the port number to run the serrver and attach to the connection object
    connection.port = int(port_entry.get())

    #Create server socket
    connection.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    connection.server_socket.bind((connection.host_ip, connection.port))
    connection.server_socket.listen()

    #Update GUI
    history_listbox.delete(0, END)
    history_listbox.insert(0, f"Server started on port {connection.port}.")
    end_button.config(state=NORMAL)
    self_broadcast_button.config(state=NORMAL)
    message_button.config(state=NORMAL)
    kick_button.config(state=NORMAL)
    ban_button.config(state=NORMAL)
    start_button.config(state=DISABLED)

    #Create a thread to continously listen for connections
    connect_thread = threading.Thread(target=connect_client, args=(connection,))
    connect_thread.start()


def end_server(connection):
    '''Begin the process of ending the server'''
    #Alert all users that the server is closing
    message_packet = create_message("DISCONNECT", "Admin (broadcast)", "Server is closing...", light_green)
    message_json = json.dumps(message_packet)
    broadcast_message(connection, message_json.encode(connection.encoder))

    #Update GUI
    history_listbox.insert(0, f"Server closing on port {connection.port}.")
    end_button.config(state=DISABLED)
    self_broadcast_button.config(state=DISABLED)
    message_button.config(state=DISABLED)
    kick_button.config(state=DISABLED)
    ban_button.config(state=DISABLED)
    start_button.config(state=NORMAL)

    #Close server socket
    connection.server_socket.close()


def connect_client(connection):
    '''Connect an incoming client to the server'''
    while True:
        try:
            client_socket, client_address = connection.server_socket.accept()
            #Check to see if the IP of the client is banned.
            if client_address[0] in connection.banned_ips:
                message_packet = create_message("DISCONNECT", "Admin (private)", "You have been banned...goodbye", light_green)
                message_json = json.dumps(message_packet)
                client_socket.send(message_json.encode(connection.encoder))

                #Clost the client socket
                client_socket.close()
            else:
                #Send a message pakcet to recieve client info
                message_packet = create_message("INFO", "Admin (private)", "Please send your name", light_green)
                message_json = json.dumps(message_packet)
                client_socket.send(message_json.encode(connection.encoder))

                #Wait for confimration message to be sent verifiying the connection
                message_json = client_socket.recv(connection.bytesize)
                process_message(connection, message_json, client_socket, client_address)
        except:
            break


def create_message(flag, name, message, color):
    '''Return a message packet to be sent'''
    message_packet = {
        "flag": flag,
        "name": name,
        "message": message,
        "color": color,
    }

    return message_packet


def process_message(connection, message_json, client_socket, client_address=(0,0)):
    '''Update server information based on a message packet flag'''
    message_packet = json.loads(message_json) #decode and turn to dict in one step!
    flag = message_packet["flag"]
    name = message_packet["name"]
    message = message_packet["message"]
    color = message_packet["color"]

    if flag == "INFO":
        #Add the new client information to the appropriate lists
        connection.client_sockets.append(client_socket)
        connection.client_ips.append(client_address[0])

        #Broadcast the new client joining and update GUI
        message_packet = create_message("MESSAGE", "Admin (broadcast)", f"{name} has joined the server!!!", light_green)
        message_json = json.dumps(message_packet)
        broadcast_message(connection, message_json.encode(connection.encoder))

        #Update server UI
        client_listbox.insert(END, f"Name: {name}        IP Addr: {client_address[0]}")

        #Now that a client has been established, start a thread to recieve messages
        recieve_thread = threading.Thread(target=recieve_message, args=(connection, client_socket,))
        recieve_thread.start()
    
    elif flag == "MESSAGE":
        #Broadcast the given message
        broadcast_message(connection, message_json)

        #Update the server UI
        history_listbox.insert(0, f"{name}: {message}")
        history_listbox.itemconfig(0, fg=color)

    elif flag == "DISCONNECT":
        #Close/remove client socket
        index = connection.client_sockets.index(client_socket)
        connection.client_sockets.remove(client_socket)
        connection.client_ips.pop(index)
        client_listbox.delete(index)
        client_socket.close()
 
        #Alert all users that the client has left the chat
        message_packet = create_message("MESSAGE", "Admin (broadcast)", f"{name} has left the server...", light_green)
        message_json = json.dumps(message_packet)
        broadcast_message(connection, message_json.encode(connection.encoder))

        #Update the server UI
        history_listbox.insert(0, f"Admin (broadcast): {name} has left the server...")

    else:
        #Catch for errors...
        history_listbox.insert(0, "Error processing message...")

        #Admin Frame Layout
        message_button = tkinter.Button(admin_frame, text="PM", borderwidth=5, width=15, font=my_font, bg=light_green, state=DISABLED, command=lambda:private_message(my_connection))
        kick_button = tkinter.Button(admin_frame, text="Kick", borderwidth=5, width=15, font=my_font, bg=light_green, state=DISABLED, command=lambda:kick_client(my_connection))
        ban_button = tkinter.Button(admin_frame, text="Ban", borderwidth=5, width=15, font=my_font, bg=light_green, state=DISABLED, command=lambda:ban_client(my_connection))

        message_button.grid(row=0, column=0, padx=5, pady=5)
        kick_button.grid(row=0, column=1, padx=5, pady=5)
        ban_button.grid(row=0, column=2, padx=5, pady=5)

       #Create a Connection object and run the root window's mainloop()
        my_connection = Connection()
        root.mainloop()

        


