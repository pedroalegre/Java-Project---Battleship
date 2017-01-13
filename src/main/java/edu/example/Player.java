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
    private String password;

    @OneToMany(mappedBy="player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
	Set<GameScore> gameScore;

    public Player() {}

    public Player(String email, String password) {
        this.userName = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String email) {
        this.userName = email;
    }

	public GameScore getGameScore(Game game) {
		return gameScore.stream().filter(gs -> gs.getGame().equals(game)).findFirst().orElse(null);
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
