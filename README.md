# CHAT JAVA RMI
## 1. This program can do:
- A private or public chat between two or more clients, who are identified by a username.
- The client can choose who the message will transfer to.
- Every 20s list of connected clients to update.
- The administrator can block, unblock, delete client in chat.
- Sending files (text, binary) between the clients.
- Show notification every time a user is added or left the chat

## 2. How to run this application:
- In chatServer folder: Run the Main.java file to start the server
- In chatClient folder: Run the LoginView.java file to create a new client to connect to the server. To create multiple clients, run LoginView.java multiple times.

## 3. To run this application on different computers, you can follow the steps below:
- 1. Check the server's IP address
- 2. In the code that initializes the rmiregistry object, replace 'localhost' with the IP address you just received. For example: "rmi://localhost:4321/RMIApp" ==> "rmi://192.168.32.100:4321/RMIApp".
- 3. Repeat the two steps mentioned in section 2.

## 4. Note:
 *Make sure computers have been connected to each other before executing the program. You can ping between machines to check.*
 *We reccommend you to disable the firewall and check for the port if is available or not if you are get an error during execution.*



