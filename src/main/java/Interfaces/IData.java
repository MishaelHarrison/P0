package Interfaces;

interface comparator<E>{
    boolean find();
}

public interface IData {
    public <E> E create(E item);
    public <E> E Read(int ID);
    public <E> E Read(comparator finder);
    public <E> E ReadList(comparator finder);
    public <E> E Update(int ID, E item);
    public <E> boolean Delete(int ID);
}
