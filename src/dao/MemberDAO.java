package dao;

import model.Member;
import util.FileHandler;
import util.AppLogger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemberDAO {
    private static final String FILE_PATH = "data/members.csv";
    private final Map<String, Member> members = new LinkedHashMap<>();

    public MemberDAO() {
        load();
    }

    private void load() {
        try {
            for (String line : FileHandler.readLines(FILE_PATH)) {
                Member m = Member.fromCsv(line);
                members.put(m.getId(), m);
            }
        } catch (IOException e) {
            AppLogger.error("Failed to load members: " + e.getMessage());
        }
    }

    private void persist() {
        try {
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (Member m : members.values()) lines.add(m.toCsv());
            FileHandler.writeLines(FILE_PATH, lines);
        } catch (IOException e) {
            AppLogger.error("Failed to save members: " + e.getMessage());
        }
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
        persist();
    }

    public Member findById(String id) {
        return members.get(id);
    }

    public boolean deleteMember(String id) {
        boolean removed = members.remove(id) != null;
        if (removed) persist();
        return removed;
    }

    public void updateMember(Member member) {
        members.put(member.getId(), member);
        persist();
    }

    public Map<String, Member> getAllMembers() {
        return members;
    }
}
