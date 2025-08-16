package br.inatel.pos.dm111.vfu.publisher;

import java.util.List;

public record UserEvent(String id, String name, String email, String password, String type, List<String> preferredCategories) {
}
