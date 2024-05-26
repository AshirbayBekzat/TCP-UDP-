import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TCPClient {
    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private static final String SELECTED = "SELECTED";
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             DataInputStream dataIn = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            String formattedDateTime = getFormattedDateTime();
            System.out.println("["+formattedDateTime+"]" + " Connection Established!!");
            System.out.println("Input command must be in this format: ");
            System.out.println("      GET <key>");
            System.out.println("      PUT <key> <value>");
            System.out.println("      DELETE <key>");
            System.out.println("      KEYS");
            System.out.println("      SELECTED <key> (to disconnect other clients)");
            System.out.println("      QUIT");

            while (true) {
                System.out.print("Enter command: ");
                String command = reader.readLine();


                String[] parts = command.split("\\s+");
                if (parts.length == 0) {
                    System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Command not recognized" + "\u001B[0m");
                    continue;
                }

                String mainCommand = parts[0];
                switch (mainCommand.toUpperCase()) {
                    case QUIT:
                        handleQuitRequest(dataOut,dataIn,formattedDateTime);
                        break;
                    case GET:
                        if (parts.length != 2) {
                            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Invalid GET command. Format: GET <key>" + "\u001B[0m");
                            continue;
                        }
                        handleGetRequest(parts[1], dataOut, dataIn, formattedDateTime);
                        break;
                    case PUT:
                        if (parts.length != 3) {
                            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Invalid PUT command. Format: PUT <key> <value>" + "\u001B[0m");
                            continue;
                        }
                        handlePutRequest(parts[1], parts[2], dataOut, dataIn, formattedDateTime);
                        break;
                    case DELETE:
                        if (parts.length != 2) {
                            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Invalid DELETE command. Format: DELETE <key>" + "\u001B[0m");
                            continue;
                        }
                        handleDeleteRequest(parts[1], dataOut, dataIn, formattedDateTime);
                        break;
                    case KEYS:
                        if (parts.length != 1) {
                            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Invalid KEYS command. Format: KEYS" + "\u001B[0m");
                            continue;
                        }
                        handleKeysRequest(dataOut, dataIn, formattedDateTime);
                        break;
                    case SELECTED:
                        if (parts.length != 2) {
                            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Invalid SELECTED command. Format: SELECTED <key>" + "\u001B[0m");
                            continue;
                        }
                        System.out.println("Klients :" +" 1,2,3");
                        handleSelectedRequest(parts[1], dataOut, dataIn, formattedDateTime);
                        break;
                    default:
                        System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " Command not recognized" + "\u001B[0m");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void handleGetRequest(String key, DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(GET + " " + key);
        String response = dataIn.readUTF();
        if (response.startsWith("Error")) {
            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " " + response + "\u001B[0m");
        } else {
            System.out.println("Response: " + response);
            System.out.println("\u001B[32m" + "["+formattedDateTime+"]" + " Command executed successfully" + "\u001B[0m");
        }
    }

    private static void handleDeleteRequest(String key, DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(DELETE + " " + key);
        String response = dataIn.readUTF();
        if(response.startsWith("Error")) {
            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " " + response + "\u001B[0m");
        } else {
            System.out.println("Response: " + response);
            System.out.println("\u001B[32m" + "["+formattedDateTime+"]" + " Command executed successfully" + "\u001B[0m");
        }
    }

    private static void handlePutRequest(String key, String value, DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(PUT + " " + key + " " + value);
        String response = dataIn.readUTF();
        if (response.startsWith("Error")) {
            System.out.println("\u001B[31m" + "[" + formattedDateTime + "]" + " " + response + "\u001B[0m");
        } else {
            System.out.println("Response: " + response);
            System.out.println("\u001B[32m" + "[" + formattedDateTime + "]" + " Command executed successfully" + "\u001B[0m");
        }
    }

    private static void handleKeysRequest(DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(KEYS);
        String response = dataIn.readUTF();
        System.out.println("Keys: " + response);
        System.out.println("\u001B[32m" + "["+formattedDateTime+"]" + " Command executed successfully" + "\u001B[0m");
    }

    private static void handleQuitRequest(DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(QUIT);
        String response = dataIn.readUTF();
        System.out.println("Response: "+ response);
        System.out.println("\u001B[32m" + "["+formattedDateTime+"]" + " Command executed successfully" + "\u001B[0m");
    }

    private static void handleSelectedRequest(String key, DataOutputStream dataOut, DataInputStream dataIn, String formattedDateTime) throws IOException {
        dataOut.writeUTF(SELECTED + " " + key);
        System.out.println("Klients :" +" 1,2,3");
        String response = dataIn.readUTF();
        System.out.println("Response: "+ response);
        if (!response.equals("No connected clients.")) {
            System.out.println("\u001B[31m" + "["+formattedDateTime+"]" + " All other clients disconnected" + "\u001B[0m");
        }
        System.out.println("\u001B[32m" + "["+formattedDateTime+"]" + " Command executed successfully" + "\u001B[0m");
    }

    private static String getFormattedDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return "[" + now.format(formatter) + "]";
    }
}
