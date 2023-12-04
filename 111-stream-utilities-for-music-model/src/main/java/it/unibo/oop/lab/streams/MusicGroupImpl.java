package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream()
                .map(Song::getSongName)
                .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet().stream()
                .filter(entry -> {
                    return entry.getValue() == year;
                })
                .map(entry -> entry.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int)songs.stream()
                .filter(song -> {
                    return song.albumName.isPresent() && song.getAlbumName().get() == albumName;
                })
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int)songs.stream()
                .filter(song ->{
                    return song.albumName.isEmpty();
                })
                .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        final var sum = songs.stream()
                .filter(song -> {
                    return song.albumName.isPresent() && song.getAlbumName().get() == albumName;
                })
                .map(Song::getDuration)
                .reduce((a,b)->a+b);
        return sum.isPresent() ? OptionalDouble.of(sum.get()/countSongs(albumName)) : OptionalDouble.empty();
    }

    @Override
    public Optional<String> longestSong() {
        final Optional<Song> stream = songs.stream()
                .max((s1,s2)->Double.compare(s1.getDuration(), s2.getDuration()));
        return stream.isPresent() ? Optional.of(stream.get().getSongName()) : Optional.empty();
    }

    @Override
    public Optional<String> longestAlbum() {
        return null;
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
