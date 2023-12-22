package client.utils;

import java.io.*;
import java.util.*;

public class Storage implements Serializable {
    private Map<String, List<Long>> recentServerToBoard;
    private Map<Long, String> boardPassword;

    /**
     * Constructor
     * Simply initializes the Map
     */
    public Storage() {
        this.recentServerToBoard = new HashMap<>();
        this.boardPassword = new HashMap<>();
        deserialize();
    }

    /**
     * Adds board with a corresponding server
     * If there is no such server it is created
     * @param server to be remembered
     * @param boardID to be remembered
     */
    public void addBoard(String server, Long boardID){
        if(!recentServerToBoard.containsKey(server)){
            recentServerToBoard.put(server, new ArrayList<>());
        }
        var boardList = recentServerToBoard.get(server);
        if(!boardList.contains(boardID)){
            boardList.add(boardID);
        }
    }

    /**
     * Removes from hashmap
     * @param server to be removed
     */
    public void removeServer(String server){
        this.recentServerToBoard.remove(server);
    }
    /**
     * Get all recent servers
     * @return Set of String, which contain server URLs
     */
    public Set<String> getAllServers(){
        return recentServerToBoard.keySet();
    }

    /**
     * Get all recent boards for a given server
     * @param server The provided server
     * @return List of board ids
     */
    public List<Long> getAllBoardsForServer(String server){
        if(recentServerToBoard.containsKey(server)) {
            return recentServerToBoard.get(server);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Removes board from recents
     * @param server target server
     * @param id target board
     */
    public void removeRecentBoard(String server, Long id){
        var curr = this.recentServerToBoard.get(server);
        for(int i = 0; i < curr.size(); ++i){
            if(curr.get(i).equals(id)){
                curr.remove(i);
                return;
            }
        }
    }
    /**
     * Helper method that reads from file
     * and serializes the object
     */
    public void serialize(){
        try {
            FileOutputStream fos = new FileOutputStream("recent.sera");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            fos.close();
        } catch (FileNotFoundException fileNotFoundException){
            System.err.println("Could not find file, when serializing");
        } catch (IOException ioException){
            System.err.println("Could not write output stream to file");
        }
    }

    /**
     * Associates password to board
     * @param id board id
     * @param password associated password
     */
    public void addBoardPass(Long id, String password){
        this.boardPassword.put(id, password);
    }

    /**
     * Utility method that removes
     * associated password to a board
     * @param id board id
     */
    public void removeBoardPass(Long id){
        this.boardPassword.remove(id);
    }

    /**
     * Utility method that retrieves saved pass
     * @param id board id
     * @return saved pass or null
     */
    public String getPasswordBoard(Long id){
        return this.boardPassword.get(id);
    }
    /**
     * Add server to hashmap if it is not already there
     * @param server to be added
     */
    public void rememberServer(String server){
        if(!recentServerToBoard.containsKey(server)){
            recentServerToBoard.put(server, new ArrayList<>());
        }
    }
    /**
     * Helper method that writes to file
     * and deserializes the object
     */
    private void deserialize(){
        try {
            FileInputStream fis = new FileInputStream("recent.sera");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            this.recentServerToBoard = ((Storage) obj).recentServerToBoard;
            this.boardPassword = ((Storage) obj).boardPassword;
        } catch (FileNotFoundException fileNotFoundException){
            System.err.println("Could not find file, when deserializing");
        } catch (IOException ioException){
            System.err.println("Could not read from input stream ");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not determine class");
        }
    }
}
