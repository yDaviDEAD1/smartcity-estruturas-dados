package smartcity;

/** Nó encadeado criado pelo projeto; não usa árvore pronta de biblioteca. */
final class NoArvore {
    Localidade valor;
    NoArvore esquerdo;
    NoArvore direito;
    NoArvore(Localidade valor) { this.valor = valor; }
}
