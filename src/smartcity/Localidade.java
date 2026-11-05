package smartcity;

/** Entidade imutável: atualizar dados significa substituir o objeto, mantendo o ID. */
public final class Localidade {
    private final int id;
    private final String nome;
    private final long populacao;

    public Localidade(int id, String nome, long populacao) {
        if (id <= 0) throw new IllegalArgumentException("O ID deve ser positivo.");
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe um nome.");
        if (populacao < 0) throw new IllegalArgumentException("A população não pode ser negativa.");
        this.id = id;
        this.nome = nome.trim();
        this.populacao = populacao;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public long getPopulacao() { return populacao; }
    @Override public String toString() { return id + " - " + nome + " (população: " + populacao + ")"; }
}
