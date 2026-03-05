class Item {
    protected String name;
    protected String effect;

    public Item(String name, String effect){
        this.name = name;
        this.effect = effect;
    }

    public void use(){
        System.out.println("Using  " + name + ": " + effect);
    }
}
