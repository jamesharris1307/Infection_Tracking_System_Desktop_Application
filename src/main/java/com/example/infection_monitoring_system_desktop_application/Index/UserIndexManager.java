package com.example.infection_monitoring_system_desktop_application.Index;

import com.example.infection_monitoring_system_desktop_application.Model.User;
import java.util.*;

public class UserIndexManager {

    private final Map<User.AccountStatus, List<User>> statusIndex = new HashMap<>();
    private final TreeSet<User> nameSortedIndex = new TreeSet<>(Comparator.comparing(User::getFirstName, Comparator.nullsLast(String::compareTo)));
    private final Trie<User> nameTrie = new Trie<>();

    public void rebuildIndexes(List<User> users) {
        statusIndex.clear();
        nameSortedIndex.clear();
        nameTrie.clear();

        for (User u : users) {
            addToIndexes(u);
        }
    }

    public void addToIndexes(User u) {
        statusIndex.computeIfAbsent(u.getAccountStatus(), k -> new ArrayList<>()).add(u);
        nameSortedIndex.add(u);
        nameTrie.insert(u.getFirstName().toLowerCase(), u);
    }

    public void removeFromIndexes(User u) {
        List<User> list = statusIndex.get(u.getAccountStatus());
        if (list != null) {
            list.remove(u);
        }
        nameSortedIndex.remove(u);
        nameTrie.remove(u.getFirstName().toLowerCase(), u);
    }

    public List<User> getByStatus(User.AccountStatus status) {
        return statusIndex.getOrDefault(status, Collections.emptyList());
    }

    public List<User> getByNamePrefix(String prefix) {
        return nameTrie.search(prefix.toLowerCase());
    }

    public Set<User> getNameSortedIndex() {
        return Collections.unmodifiableSet(nameSortedIndex);
    }
}
