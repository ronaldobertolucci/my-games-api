package io.github.ronaldobertolucci.mygames.service.mygame;

import io.github.ronaldobertolucci.mygames.exception.ForbiddenException;
import io.github.ronaldobertolucci.mygames.exception.UnprocessableEntity;
import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.game.GameRepository;
import io.github.ronaldobertolucci.mygames.model.mygame.*;
import io.github.ronaldobertolucci.mygames.model.platform.Platform;
import io.github.ronaldobertolucci.mygames.model.platform.PlatformRepository;
import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.source.SourceRepository;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyGameService {

    @Autowired
    private MyGameRepository myGameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private SourceRepository sourceRepository;

    public Page<MyGameDto> findByUser(Pageable pageable, String username) {
        User user = userRepository.findByUsername(username);
        Page<MyGame> myGames = myGameRepository.findByUser(user, pageable);
        return myGames.map(MyGameDto::new);
    }

    public MyGameDto detail(Long id, String username) {
        MyGame myGame = myGameRepository.getReferenceById(id);

        if (!myGame.getUser().getUsername().equals(username) && !isAdmin()) {
            throw new ForbiddenException("You don't have access to this game");
        }

        return new MyGameDto(myGame);
    }

    @Transactional
    public MyGameDto save(SaveMyGameDto dto, String username) {
        MyGame myGame = new MyGame();
        setUser(username, myGame);
        setGame(dto.gameId(), myGame);
        setPlatform(dto.platformId(), myGame);
        setSource(dto.sourceId(), myGame);
        setStatusToNewRecords(dto.status(), myGame);

        myGameRepository.save(myGame);
        return new MyGameDto(myGame);
    }

    @Transactional
    public MyGameDto update(UpdateMyGameDto dto, String username) {
        MyGame myGame = myGameRepository.getReferenceById(dto.id());

        if (!myGame.getUser().getUsername().equals(username) && !isAdmin()) {
            throw new ForbiddenException("You don't have access to this game");
        }

        setGame(dto.gameId(), myGame);
        setPlatform(dto.platformId(), myGame);
        setSource(dto.sourceId(), myGame);

        myGame.setStatus(dto.status());
        return new MyGameDto(myGame);
    }

    @Transactional
    public MyGameDto updateStatus(Long id, MyGamesStatusDto dto, String username) {
        MyGame myGame = myGameRepository.getReferenceById(id);

        if (!myGame.getUser().getUsername().equals(username) && !isAdmin()) {
            throw new ForbiddenException("You don't have access to this game");
        }

        myGame.setStatus(dto.status());
        return new MyGameDto(myGame);
    }

    @Transactional
    public void delete(Long id, String username) {
        MyGame myGame = myGameRepository.getReferenceById(id);

        if (!myGame.getUser().getUsername().equals(username) && !isAdmin()) {
            throw new ForbiddenException("You don't have access to this game");
        }

        myGameRepository.delete(myGame);
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private void setUser(String username, MyGame myGame) {
        try {
            User user = userRepository.findByUsername(username);
            user.toString(); // forçando o carregamento
            myGame.setUser(user);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }
    
    private void setGame(Long gameId, MyGame myGame) {
        try {
            Game game = gameRepository.getReferenceById(gameId);
            game.toString(); // forçando o carregamento
            myGame.setGame(game);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

    private void setPlatform(Long platformId, MyGame myGame) {
        try {
            Platform platform = platformRepository.getReferenceById(platformId);
            platform.toString(); // forçando o carregamento
            myGame.setPlatform(platform);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

    private void setSource(Long sourceId, MyGame myGame) {
        try {
            Source source = sourceRepository.getReferenceById(sourceId);
            source.toString(); // forçando o carregamento
            myGame.setSource(source);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

    private void setStatusToNewRecords(Status status, MyGame myGame) {
        if (status != null) {
            myGame.setStatus(status);
            return;
        }

        myGame.setStatus(Status.NOT_PLAYED);
    }

}
