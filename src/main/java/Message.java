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


    public Message(String origUsername, String destUsername, String data, String flag) {
        this.username = origUsername;
        this.data = data;
        this.receiver = destUsername;
        this.flag = flag;
    }


    public String getData() {return data;}
    public String getUsername() {return username;}
    public String getReceiver() {return receiver;}
    public boolean isInfoName() {return (Objects.equals(flag, "isInfoName"));}
    public boolean isPublicText() {return (Objects.equals(flag, "isPublicText"));}
    public boolean isPrivateText() {return (Objects.equals(flag, "isPrivateText"));};
    public boolean isUpdateFriends() {return (Objects.equals(flag, "isUpdateFriends"));}
    public String getFlag() {return flag;}
}
