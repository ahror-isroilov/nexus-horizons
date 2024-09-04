package multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameState implements Serializable {
    private PlayerState player1;
    private PlayerState player2;
    private List<EnemyState> enemies;
    // Add other necessary game state information
}