package smartcity;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/** ABB própria. Altura em arestas: vazia = -1, folha = 0; raiz no nível 0. */
public final class ArvoreBinariaBusca {
    private NoArvore raiz;

    public static final class Busca {
        public final Localidade localidade;
        public final int comparacoes;
        Busca(Localidade localidade, int comparacoes) { this.localidade = localidade; this.comparacoes = comparacoes; }
    }
    public boolean vazia() { return raiz == null; }
    public Localidade raiz() { return vazia() ? null : raiz.valor; }
    public Busca buscarComComparacoes(int id) {
        int comparacoes = 0;
        NoArvore atual = raiz;
        while (atual != null) {
            comparacoes++; // Uma comparação de chave por nó visitado.
            int cmp = Integer.compare(id, atual.valor.getId());
            if (cmp == 0) return new Busca(atual.valor, comparacoes);
            atual = cmp < 0 ? atual.esquerdo : atual.direito;
        }
        return new Busca(null, comparacoes);
    }
    public Localidade buscar(int id) { return buscarComComparacoes(id).localidade; }
    public void inserir(Localidade valor) {
        if (valor == null) throw new IllegalArgumentException("Localidade obrigatória.");
        if (raiz == null) { raiz = new NoArvore(valor); return; }
        NoArvore atual = raiz;
        while (true) {
            int cmp = Integer.compare(valor.getId(), atual.valor.getId());
            if (cmp == 0) throw new IllegalArgumentException("ID já cadastrado.");
            if (cmp < 0) {
                if (atual.esquerdo == null) { atual.esquerdo = new NoArvore(valor); return; }
                atual = atual.esquerdo;
            } else {
                if (atual.direito == null) { atual.direito = new NoArvore(valor); return; }
                atual = atual.direito;
            }
        }
    }
    public void atualizar(Localidade valor) {
        if (valor == null) throw new IllegalArgumentException("Localidade obrigatória.");
        NoArvore atual = raiz;
        while (atual != null) {
            int cmp = Integer.compare(valor.getId(), atual.valor.getId());
            if (cmp == 0) { atual.valor = valor; return; }
            atual = cmp < 0 ? atual.esquerdo : atual.direito;
        }
        throw new IllegalArgumentException("ID não cadastrado.");
    }
    public void remover(int id) {
        if (buscar(id) == null) throw new IllegalArgumentException("ID não cadastrado.");
        raiz = remover(raiz, id);
    }
    private NoArvore remover(NoArvore no, int id) {
        if (id < no.valor.getId()) no.esquerdo = remover(no.esquerdo, id);
        else if (id > no.valor.getId()) no.direito = remover(no.direito, id);
        else {
            if (no.esquerdo == null) return no.direito; // Folha ou um filho.
            if (no.direito == null) return no.esquerdo;
            NoArvore sucessor = no.direito;
            while (sucessor.esquerdo != null) sucessor = sucessor.esquerdo;
            no.valor = sucessor.valor; // Dois filhos: sucessor em ordem.
            no.direito = remover(no.direito, sucessor.valor.getId());
        }
        return no;
    }
    public Localidade menor() { NoArvore n = raiz; if (n == null) return null; while (n.esquerdo != null) n = n.esquerdo; return n.valor; }
    public Localidade maior() { NoArvore n = raiz; if (n == null) return null; while (n.direito != null) n = n.direito; return n.valor; }
    public int quantidade() { return contar(raiz); }
    private int contar(NoArvore n) { return n == null ? 0 : 1 + contar(n.esquerdo) + contar(n.direito); }
    public int altura() { return altura(raiz); }
    private int altura(NoArvore n) { return n == null ? -1 : 1 + Math.max(altura(n.esquerdo), altura(n.direito)); }
    public int alturaEsquerda() { return raiz == null ? -1 : altura(raiz.esquerdo); }
    public int alturaDireita() { return raiz == null ? -1 : altura(raiz.direito); }
    public int nivel(int id) {
        NoArvore n = raiz; int nivel = 0;
        while (n != null) { if (id == n.valor.getId()) return nivel; n = id < n.valor.getId() ? n.esquerdo : n.direito; nivel++; }
        return -1;
    }
    public List<Localidade> folhas() { List<Localidade> r = new ArrayList<>(); classificar(raiz, true, r); return r; }
    public List<Localidade> internos() { List<Localidade> r = new ArrayList<>(); classificar(raiz, false, r); return r; }
    private void classificar(NoArvore n, boolean folhas, List<Localidade> r) {
        if (n == null) return;
        classificar(n.esquerdo, folhas, r);
        if ((n.esquerdo == null && n.direito == null) == folhas) r.add(n.valor);
        classificar(n.direito, folhas, r);
    }
    public List<Localidade> emOrdem() { return percurso(1); }
    public List<Localidade> preOrdem() { return percurso(0); }
    public List<Localidade> posOrdem() { return percurso(2); }
    private List<Localidade> percurso(int tipo) { List<Localidade> r = new ArrayList<>(); percorrer(raiz, tipo, r); return r; }
    private void percorrer(NoArvore n, int tipo, List<Localidade> r) {
        if (n == null) return;
        if (tipo == 0) r.add(n.valor);
        percorrer(n.esquerdo, tipo, r);
        if (tipo == 1) r.add(n.valor);
        percorrer(n.direito, tipo, r);
        if (tipo == 2) r.add(n.valor);
    }
    public List<Localidade> porNiveis() {
        List<Localidade> r = new ArrayList<>(); Queue<NoArvore> fila = new ArrayDeque<>();
        if (raiz != null) fila.add(raiz);
        while (!fila.isEmpty()) {
            NoArvore n = fila.remove(); r.add(n.valor);
            if (n.esquerdo != null) fila.add(n.esquerdo);
            if (n.direito != null) fila.add(n.direito);
        }
        return r;
    }
    public boolean balanceada() { return alturaBalanceada(raiz) != Integer.MIN_VALUE; }
    private int alturaBalanceada(NoArvore n) {
        if (n == null) return -1;
        int e = alturaBalanceada(n.esquerdo), d = alturaBalanceada(n.direito);
        if (e == Integer.MIN_VALUE || d == Integer.MIN_VALUE || Math.abs(e - d) > 1) return Integer.MIN_VALUE;
        return 1 + Math.max(e, d);
    }
    public void balancear() { List<Localidade> ordenados = emOrdem(); raiz = construir(ordenados, 0, ordenados.size() - 1); }
    private NoArvore construir(List<Localidade> lista, int inicio, int fim) {
        if (inicio > fim) return null;
        int meio = inicio + (fim - inicio) / 2; NoArvore n = new NoArvore(lista.get(meio));
        n.esquerdo = construir(lista, inicio, meio - 1); n.direito = construir(lista, meio + 1, fim); return n;
    }
    /** Vetor real com índice zero reservado; limita alocação para não esgotar memória. */
    public Localidade[] vetorPorNiveis(int limite) {
        Map<BigInteger, Localidade> posicoes = posicoesVetor();
        BigInteger maior = BigInteger.ZERO;
        for (BigInteger i : posicoes.keySet()) maior = maior.max(i);
        if (limite < 0 || maior.compareTo(BigInteger.valueOf(limite)) > 0 || maior.compareTo(BigInteger.valueOf(Integer.MAX_VALUE - 1)) > 0)
            throw new IllegalArgumentException("Vetor excede o limite de " + limite + " posições; use o relatório esparso.");
        Localidade[] vetor = new Localidade[maior.intValue() + 1];
        for (Map.Entry<BigInteger, Localidade> e : posicoes.entrySet()) vetor[e.getKey().intValue()] = e.getValue();
        return vetor;
    }
    /** Índices lógicos exatos, em BFS; não aloca as posições vazias. */
    public Map<BigInteger, Localidade> posicoesVetor() {
        Map<BigInteger, Localidade> r = new LinkedHashMap<>();
        Queue<NoArvore> nos = new ArrayDeque<>(); Queue<BigInteger> indices = new ArrayDeque<>();
        if (raiz != null) { nos.add(raiz); indices.add(BigInteger.ONE); }
        while (!nos.isEmpty()) {
            NoArvore n = nos.remove(); BigInteger i = indices.remove(); r.put(i, n.valor);
            if (n.esquerdo != null) { nos.add(n.esquerdo); indices.add(i.shiftLeft(1)); }
            if (n.direito != null) { nos.add(n.direito); indices.add(i.shiftLeft(1).add(BigInteger.ONE)); }
        }
        return r;
    }
}
