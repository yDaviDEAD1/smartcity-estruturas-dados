package smartcity;

import java.math.BigInteger;
import java.util.*;

/** Fachada: toda mutação passa aqui, mantendo o mesmo conjunto de IDs nas estruturas. */
public final class SmartCity {
    private final ArvoreBinariaBusca arvore = new ArvoreBinariaBusca();
    private final Grafo grafo = new Grafo();
    public void cadastrar(int id, String nome, long populacao) {
        Localidade l = new Localidade(id, nome, populacao);
        if (arvore.buscar(id) != null) throw new IllegalArgumentException("ID já cadastrado.");
        arvore.inserir(l); grafo.adicionarVertice(id);
    }
    public Localidade consultar(int id) { Localidade l = arvore.buscar(id); if (l == null) throw new IllegalArgumentException("Localidade inexistente: " + id); return l; }
    public void atualizar(int id, String nome, long populacao) { consultar(id); arvore.atualizar(new Localidade(id, nome, populacao)); }
    public void remover(int id) { consultar(id); grafo.removerVertice(id); arvore.remover(id); }
    public void conectar(int origem, int destino, double km, double min, boolean dupla) { consultar(origem); consultar(destino); grafo.adicionarAresta(origem, destino, km, min, dupla); }
    public void desconectar(int origem, int destino, boolean dupla) { consultar(origem); consultar(destino); grafo.removerAresta(origem, destino, dupla); }
    public void alterarPesos(int origem, int destino, double km, double min, boolean dupla) { consultar(origem); consultar(destino); grafo.alterarPesos(origem, destino, km, min, dupla); }
    public void balancear() { arvore.balancear(); }
    public ArvoreBinariaBusca.Busca busca(int id) { return arvore.buscarComComparacoes(id); }
    public Grafo.ResultadoBfs bfs(int origem, int destino) { consultar(origem); consultar(destino); return grafo.bfs(origem, destino); }
    public List<Integer> dfs(int origem) { consultar(origem); return grafo.dfs(origem); }
    public Grafo.Rota rota(int origem, int destino, Grafo.Criterio criterio) {
        consultar(origem); consultar(destino);
        return criterio == Grafo.Criterio.CONEXOES ? grafo.bfs(origem, destino).rota : grafo.dijkstra(origem, destino, criterio);
    }
    public String descreverRota(Grafo.Rota rota) {
        if (!rota.encontrada()) return "Sem caminho dirigido entre as localidades.";
        List<String> nomes = new ArrayList<>(); for (int id : rota.caminho) nomes.add(id + " (" + consultar(id).getNome() + ")");
        return String.join(" -> ", nomes) + String.format(Locale.ROOT, " | %d conexões | %.2f km | %.2f min", rota.conexoes(), rota.distancia, rota.tempo);
    }
    public String compararRotas(int origem, int destino) {
        StringBuilder r = new StringBuilder("RELATÓRIO D — ROTAS\n");
        r.append(String.format("%-18s | %-24s | %8s | %10s | %10s%n", "Critério", "IDs do caminho", "Arestas", "km", "min"));
        for (Grafo.Criterio c : Grafo.Criterio.values()) {
            Grafo.Rota rota = rota(origem, destino, c);
            r.append(rota.encontrada() ? String.format(Locale.ROOT, "%-18s | %-24s | %8d | %10.2f | %10.2f%n", c, rota.caminho, rota.conexoes(), rota.distancia, rota.tempo) : c + " | SEM CAMINHO\n");
            if (rota.encontrada()) r.append("  ").append(descreverRota(rota)).append('\n');
        }
        return r.append("BFS minimiza arestas; Dijkstra minimiza a soma do peso escolhido.\n").toString();
    }
    public String relatorioCadastro() {
        List<Localidade> lista = arvore.emOrdem(); BigInteger total = BigInteger.ZERO;
        long maior = -1, menor = Long.MAX_VALUE;
        for (Localidade l : lista) { total = total.add(BigInteger.valueOf(l.getPopulacao())); maior = Math.max(maior, l.getPopulacao()); menor = Math.min(menor, l.getPopulacao()); }
        List<Localidade> maiores = new ArrayList<>(), menores = new ArrayList<>();
        for (Localidade l : lista) { if (l.getPopulacao() == maior) maiores.add(l); if (l.getPopulacao() == menor) menores.add(l); }
        return "RELATÓRIO A — CADASTRO\nTotal: " + lista.size() + "\nOrdenadas por ID: " + lista + "\nPopulação total: " + total + "\nMaior população: " + maiores + "\nMenor população: " + menores;
    }
    public String relatorioArvore(int... ids) {
        StringBuilder r = new StringBuilder("RELATÓRIO B — ÁRVORE\n");
        r.append("Vazia: ").append(arvore.vazia()).append("\nRaiz: ").append(arvore.raiz()).append("\nNós: ").append(arvore.quantidade());
        r.append("\nAltura: ").append(arvore.altura()).append(" (arestas; vazia = -1)");
        r.append("\nAlturas das subárvores da raiz: esquerda=").append(arvore.alturaEsquerda()).append(", direita=").append(arvore.alturaDireita());
        r.append("\nFolhas: ").append(arvore.folhas()).append("\nInternos: ").append(arvore.internos());
        r.append("\nBalanceada em todos os nós: ").append(arvore.balanceada());
        r.append("\nMenor ID: ").append(arvore.menor()).append("\nMaior ID: ").append(arvore.maior());
        for (int id : ids) { ArvoreBinariaBusca.Busca b = busca(id); r.append("\nBusca ").append(id).append(": ").append(b.localidade).append(" | comparações de chave: ").append(b.comparacoes).append(" | nível: ").append(arvore.nivel(id)); }
        return r.toString();
    }
    public String percursos() { return "Pré-ordem: " + arvore.preOrdem() + "\nEm ordem: " + arvore.emOrdem() + "\nPós-ordem: " + arvore.posOrdem() + "\nPor níveis: " + arvore.porNiveis(); }
    public String relatorioRede() {
        int max = -1; List<Integer> maiores = new ArrayList<>();
        for (int id : grafo.vertices()) { int grau = grafo.grauEntrada(id) + grafo.grauSaida(id); if (grau > max) { max = grau; maiores.clear(); } if (grau == max) maiores.add(id); }
        return "RELATÓRIO C — TRANSPORTES\nVértices: " + grafo.vertices().size() + "\nArestas direcionadas: " + grafo.arestas().size() + "\nIsolados: " + grafo.isolados() + "\nComponentes (projeção não direcionada): " + grafo.componentesNaoDirecionadas() + "\nRede não direcionada conexa: " + grafo.conexoNaoDirecionado() + "\nCiclo direcionado: " + grafo.temCicloDirecionado() + "\nMaior grau total (entrada + saída): " + maiores + " | grau=" + Math.max(0, max);
    }
    public String conexoes() { return "Vértices: " + grafo.vertices() + "\nArestas: " + grafo.arestas(); }
    public String vizinhanca(int id, int destino) { consultar(id); consultar(destino); return "Vizinhos de saída: " + grafo.vizinhos(id) + "\nGrau entrada: " + grafo.grauEntrada(id) + "\nGrau saída: " + grafo.grauSaida(id) + "\nAdjacente a " + destino + ": " + grafo.adjacentes(id, destino); }
    public boolean consistente() {
        List<Integer> ids = new ArrayList<>(); for (Localidade l : arvore.emOrdem()) ids.add(l.getId());
        if (!ids.equals(grafo.vertices())) return false;
        for (Grafo.Aresta a : grafo.arestas()) if (arvore.buscar(a.origem) == null || arvore.buscar(a.destino) == null) return false;
        return true;
    }
    public String armazenamento() {
        ArvoreBinariaBusca balanceada = new ArvoreBinariaBusca(), linear = new ArvoreBinariaBusca();
        for (Localidade l : arvore.emOrdem()) { balanceada.inserir(l); linear.inserir(l); } balanceada.balancear();
        return "Índice 0 reservado. Esquerdo=2*i; direito=2*i+1; pai=i/2.\nAtual: " + resumoVetor(arvore) + "\nBalanceada: " + resumoVetor(balanceada) + "\nCrescente (cadeia à direita): " + resumoVetor(linear) + "\nPosições ocupadas da árvore atual (ordem por níveis): " + arvore.posicoesVetor() + "\n" + vetorReal();
    }
    private String vetorReal() {
        try { return "Vetor real (inclui índice 0 reservado): " + Arrays.toString(arvore.vetorPorNiveis(4095)); }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }
    private String resumoVetor(ArvoreBinariaBusca a) {
        BigInteger posicoes = BigInteger.ZERO; for (BigInteger i : a.posicoesVetor().keySet()) posicoes = posicoes.max(i);
        return a.quantidade() + " nós; " + posicoes + " posições úteis; " + posicoes.subtract(BigInteger.valueOf(a.quantidade())) + " vazias (mais índice 0 reservado)";
    }
    public static SmartCity demonstracao() {
        SmartCity s = new SmartCity();
        s.cadastrar(40, "Jardim", 8000); s.cadastrar(20, "Estação", 12000); s.cadastrar(60, "Universidade", 6000);
        s.cadastrar(10, "Centro", 20000); s.cadastrar(30, "Hospital", 5000); s.cadastrar(50, "Terminal", 4000);
        s.cadastrar(70, "Porto", 9000); s.cadastrar(80, "Ilha", 1000);
        s.conectar(10, 40, 20, 30, false);
        s.conectar(10, 20, 2, 12, true); s.conectar(20, 30, 2, 12, false); s.conectar(30, 40, 2, 12, false);
        s.conectar(10, 50, 8, 3, false); s.conectar(50, 40, 8, 3, false);
        s.conectar(40, 60, 3, 5, true); s.conectar(60, 70, 4, 7, false);
        return s;
    }
}
