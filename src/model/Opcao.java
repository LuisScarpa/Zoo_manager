package model;


public class Opcao {
    public final Integer id;
    public final String rotulo;

    public Opcao(Integer id, String rotulo) {
        this.id = id;
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
