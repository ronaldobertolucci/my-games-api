package br.com.bertolucci.mygames.model.genre;


public record GenreDto(
        Long id,
        String name
) {
    public GenreDto(Genre genre) {
        this(genre.getId(), genre.getName());
    }
}
