package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.platform.Platform;
import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "my_games")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"user", "game", "platform", "source"})
public class MyGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_id")
    private Platform platform;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    private Source source;

    @Enumerated(EnumType.STRING)
    private Status status;
}