package br.inatel.pos.dm111.vfu.persistence.user;

public record User(String id, String name, String email, String password, String type) {
}
