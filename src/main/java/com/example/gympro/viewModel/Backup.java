package com.example.gympro.viewModel;

public class Backup {
    private int id;
    private String name;
    private String created;
    private String size;

    public Backup(int id, String name, String created, String size) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.size = size;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCreated() { return created; }
    public String getSize() { return size; }
}
