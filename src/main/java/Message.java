import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    private String data, username, receiver, flag;


    /*
    Default constructor
     */
    public Message(String origUsername, String destUsername, String data, String flag) {
        this.username = origUsername;
        this.data = data;
        this.receiver = destUsername;
        this.flag = flag;
    }


    /*
    Helper Functions
     */

    // Returns the data string
    public String getData() {return data;}
    // Returns the username string
    public String getUsername() {return username;}
    // Returns the receiver string
    public String getReceiver() {return receiver;}
    // Returns true if flag equals to isCheckUniqueName, false otherwise
    public boolean isCheckUniqueName() {return Objects.equals(flag, "isCheckUniqueName");}
    // Returns true if flag equals to isInfoName, false otherwise
    public boolean isInfoName() {return (Objects.equals(flag, "isInfoName"));}
    // Returns true if flag equals to isPublicText, false otherwise
    public boolean isPublicText() {return (Objects.equals(flag, "isPublicText"));}
    // Returns true if flag equals to isPrivateText, false otherwise
    public boolean isPrivateText() {return (Objects.equals(flag, "isPrivateText"));}
    // Returns true if flag equals to isUpdateFriends, false otherwise
    public boolean isUpdateFriends() {return (Objects.equals(flag, "isUpdateFriends"));}
    // Returns true if flag equals to isNewGroupAddMember
    public boolean isNewGroupAddMember() {return (Objects.equals(flag, "isNewGroupAddMember"));}
    // Returns true if flag equals to isNewGroup
    public boolean isUpdateGroupList() {return (Objects.equals(flag, "isUpdateGroupList"));}

}
