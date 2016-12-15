/**
 * Created by Pedro on 07-Dec-16.
 */

package edu.example;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String userName;

    public Player() {}

    public Player(String email) {
        this.userName = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String email) {
        this.userName = email;
    }

    @OneToMany(mappedBy="player", fetch = FetchType.EAGER)
    Set<GamePlayer> players;
}
