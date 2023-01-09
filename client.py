def send_message():
    '''Send a message to the server to be broadcast'''
    while True:
        message = input("")
        client_socket.send(message.encode(ENCODER))

def recieve_message():
    '''Recieve an incoming message from the server'''
    while True:
        try:
            #Recieve an incoming message from the server.
            message = client_socket.recv(BYTESIZE).decode(ENCODER)

            #Check for the name flag, else show the message
            if message == "NAME":
                name = input("What is your name: ")
                client_socket.send(name.encode(ENCODER))
            else:
                print(message)
        except:
            #An error occured, close the connection
            print("An error occured...")
            client_socket.close()
            break
#Create threads to continuously send and recieve messages
recieve_thread = threading.Thread(target=recieve_message)
send_thread = threading.Thread(target=send_message)

#Start the client
recieve_thread.start()
send_thread.start()

#Client Side Advanced GUI Chat Room
import tkinter, socket, threading, json
from tkinter import DISABLED, VERTICAL, END, NORMAL, StringVar

#Define window
root = tkinter.Tk()
root.title("Chat Client")
root.iconbitmap("message_icon.ico")
root.geometry("600x600")
root.resizable(0,0)

#Define fonts and colors
my_font = ('SimSun', 14)
black = "#010101"
light_green = "#1fc742"
white = "#ffffff"
red = "#ff3855"
orange = "#ffaa1d"
yellow = "#fff700"
green = "#1fc742"
blue = "#5dadec"
purple = "#9c51b6"
root.config(bg=black)


class Connection():
    '''A class to store a connection - a client socket and pertinent information'''
    def __init__(self):
        self.encoder = "utf-8"
        self.bytesize = 1024


#Define Functions
def connect(connection):
    '''Connect to a server at a given ip/port address'''
    #Clear any previous chats
    my_listbox.delete(0, END)

    #Get required information for connection from input fields
    connection.name = name_entry.get()
    connection.target_ip = ip_entry.get()
    connection.port = port_entry.get()
    connection.color = color.get()

    try:
        #Create a client socket
        connection.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        connection.client_socket.connect((connection.target_ip, int(connection.port)))

        #Recieve an incoming message packet from the server
        message_json = connection.client_socket.recv(connection.bytesize)
        process_message(connection, message_json)
    except:
        my_listbox.insert(0, "Connection not established...Goodbye.")


def disconnect(connection):
    '''Disconnect the client from the server'''
    #Create a message packet to be sent
    message_packet = create_message("DISCONNECT", connection.name, "I am leaving.", connection.color)
    message_json = json.dumps(message_packet)
    connection.client_socket.send(message_json.encode(connection.encoder))

    #Disable GUI for chat
    gui_end()


def gui_start():
    '''Officially start connection by updating GUI'''
    connect_button.config(state=DISABLED)
    disconnect_button.config(state=NORMAL)
    send_button.config(state=NORMAL)
    name_entry.config(state=DISABLED)
    ip_entry.config(state=DISABLED)
    port_entry.config(state=DISABLED)

    for button in color_buttons:
        button.config(state=DISABLED)


def gui_end():
    '''Officially end conneciton by updating GUI'''
    connect_button.config(state=NORMAL)
    disconnect_button.config(state=DISABLED)
    send_button.config(state=DISABLED)
    name_entry.config(state=NORMAL)
    ip_entry.config(state=NORMAL)
    port_entry.config(state=NORMAL)

    for button in color_buttons:
        button.config(state=NORMAL)
        
def create_message(flag, name, message, color):
    '''Return a message packet to be sent'''
    message_packet = {
        "flag": flag,
        "name": name,
        "message": message,
        "color": color,
    }
    return message_packet

def process_message(connection, message_json):
    '''Update the client based on message packet flag'''
    #Update the chat history first by unpacking the json message.
    message_packet = json.loads(message_json) #decode and turn to dict in one step!
    flag = message_packet["flag"]
    name = message_packet["name"]
    message = message_packet["message"]
    color = message_packet["color"]

    if flag == "INFO":
        #Server is asking for information to verify connection.  Send the info.
        message_packet = create_message("INFO", connection.name, "Joins the server!", connection.color)
        message_json = json.dumps(message_packet)
        connection.client_socket.send(message_json.encode(connection.encoder))

        #Enable GUI for chat
        gui_start()

        #Create a thread to coninousuly recieve messages from the server
        recieve_thread = threading.Thread(target=recieve_message, args=(connection,))
        recieve_thread.start()
    
    elif flag == "MESSAGE":
        #Server has sent a message so display it.
        my_listbox.insert(0, f"{name}: {message}")
        my_listbox.itemconfig(0, fg=color)


    elif flag == "DISCONNECT":
        #Server is asking you to leave.
        my_listbox.insert(0, f"{name}: {message}")
        my_listbox.itemconfig(0, fg=color)
        disconnect(connection)

    else:
        #Catch for errors...
        my_listbox.insert(0, "Error processing message...")
        
def send_message(connection):
    '''Send a message to the server'''
    #Send the message to the server
    message_packet = create_message("MESSAGE", connection.name, input_entry.get(), connection.color)
    message_json = json.dumps(message_packet)
    connection.client_socket.send(message_json.encode(connection.encoder))

    #Clear the input entry
    input_entry.delete(0, END)

def recieve_message(connection):
    '''Recieve a message from the server'''
    while True:
        #Recive an incoming message packet from the server
        try:
            #Recive an incoming message packet
            message_json = connection.client_socket.recv(connection.bytesize)
            process_message(connection, message_json)
        except:
            #Cannot recive message, clost the connection and break
            my_listbox.insert(0, "Connection has been closed...Goodbye.")
            break
