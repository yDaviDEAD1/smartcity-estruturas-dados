package smartcity;

import java.util.*;

/** Grafo dirigido próprio, representado por listas de adjacência. */
public final class Grafo {
    private final Map<Integer, List<Aresta>> adj = new HashMap<>();
    public enum Criterio { CONEXOES, DISTANCIA, TEMPO }
    public static final class Aresta {
        public final int origem, destino;
        public final double distancia, tempo;
        Aresta(int origem, int destino, double distancia, double tempo) {
            this.origem = origem; this.destino = destino; this.distancia = distancia; this.tempo = tempo;
        }
        @Override public String toString() { return origem + " -> " + destino + " | " + distancia + " km | " + tempo + " min"; }
    }
    public static final class Rota {
        public final List<Integer> caminho;
        public final double distancia, tempo;
        Rota(List<Integer> caminho, double distancia, double tempo) {
            this.caminho = Collections.unmodifiableList(new ArrayList<>(caminho)); this.distancia = distancia; this.tempo = tempo;
        }
        public boolean encontrada() { return !caminho.isEmpty(); }
        public int conexoes() { return encontrada() ? caminho.size() - 1 : 0; }
    }
    public static final class ResultadoBfs {
        public final List<Integer> ordem;
        public final Map<Integer, Integer> niveis;
        public final List<Integer> naoAlcancados;
        public final Rota rota;
        ResultadoBfs(List<Integer> ordem, Map<Integer, Integer> niveis, List<Integer> naoAlcancados, Rota rota) {
            this.ordem = Collections.unmodifiableList(ordem);
            this.niveis = Collections.unmodifiableMap(niveis);
            this.naoAlcancados = Collections.unmodifiableList(naoAlcancados); this.rota = rota;
        }
    }
    public void adicionarVertice(int id) {
        if (id <= 0 || adj.containsKey(id)) throw new IllegalArgumentException("Vértice inválido ou duplicado.");
        adj.put(id, new ArrayList<Aresta>());
    }
    private void exigir(int id) { if (!adj.containsKey(id)) throw new IllegalArgumentException("Vértice inexistente: " + id); }
    public void removerVertice(int id) {
        exigir(id); adj.remove(id);
        for (List<Aresta> lista : adj.values()) lista.removeIf(a -> a.destino == id);
    }
    public List<Integer> vertices() { List<Integer> r = new ArrayList<>(adj.keySet()); Collections.sort(r); return r; }
    public List<Aresta> vizinhos(int id) { exigir(id); return Collections.unmodifiableList(new ArrayList<>(adj.get(id))); }
    public List<Aresta> arestas() { List<Aresta> r = new ArrayList<>(); for (int id : vertices()) r.addAll(adj.get(id)); return r; }
    public Aresta aresta(int origem, int destino) {
        exigir(origem); exigir(destino);
        for (Aresta a : adj.get(origem)) if (a.destino == destino) return a;
        return null;
    }
    public boolean adjacentes(int origem, int destino) { return aresta(origem, destino) != null; }
    private void validarPesos(double distancia, double tempo) {
        if (!Double.isFinite(distancia) || !Double.isFinite(tempo) || distancia < 0 || tempo < 0)
            throw new IllegalArgumentException("Os pesos devem ser finitos e não negativos.");
    }
    public void adicionarAresta(int origem, int destino, double distancia, double tempo, boolean dupla) {
        exigir(origem); exigir(destino); validarPesos(distancia, tempo);
        if (origem == destino) throw new IllegalArgumentException("Este sistema não permite laços na própria localidade.");
        // Valida ambas antes de alterar: falha não deixa uma conexão parcial.
        if (adjacentes(origem, destino) || (dupla && adjacentes(destino, origem)))
            throw new IllegalArgumentException("Aresta duplicada. Altere ou remova a conexão existente.");
        inserirOrdenada(new Aresta(origem, destino, distancia, tempo));
        if (dupla) inserirOrdenada(new Aresta(destino, origem, distancia, tempo));
    }
    private void inserirOrdenada(Aresta a) {
        List<Aresta> lista = adj.get(a.origem); int i = 0;
        while (i < lista.size() && lista.get(i).destino < a.destino) i++;
        lista.add(i, a);
    }
    public void removerAresta(int origem, int destino, boolean dupla) {
        if (!adjacentes(origem, destino) || (dupla && !adjacentes(destino, origem)))
            throw new IllegalArgumentException("Conexão solicitada inexistente.");
        adj.get(origem).removeIf(a -> a.destino == destino);
        if (dupla) adj.get(destino).removeIf(a -> a.destino == origem);
    }
    public void alterarPesos(int origem, int destino, double distancia, double tempo, boolean dupla) {
        validarPesos(distancia, tempo);
        if (!adjacentes(origem, destino) || (dupla && !adjacentes(destino, origem)))
            throw new IllegalArgumentException("Conexão solicitada inexistente.");
        removerAresta(origem, destino, dupla); adicionarAresta(origem, destino, distancia, tempo, dupla);
    }
    public int grauSaida(int id) { exigir(id); return adj.get(id).size(); }
    public int grauEntrada(int id) { exigir(id); int n = 0; for (List<Aresta> lista : adj.values()) for (Aresta a : lista) if (a.destino == id) n++; return n; }
    public List<Integer> isolados() {
        Set<Integer> conectados = new HashSet<>();
        for (Aresta a : arestas()) { conectados.add(a.origem); conectados.add(a.destino); }
        List<Integer> r = vertices(); r.removeIf(conectados::contains); return r;
    }
    public ResultadoBfs bfs(int origem, int destino) {
        exigir(origem); exigir(destino);
        Queue<Integer> fila = new ArrayDeque<>(); Map<Integer, Integer> niveis = new LinkedHashMap<>();
        Map<Integer, Integer> pais = new HashMap<>(); List<Integer> ordem = new ArrayList<>();
        fila.add(origem); niveis.put(origem, 0);
        while (!fila.isEmpty()) {
            int u = fila.remove(); ordem.add(u);
            for (Aresta a : adj.get(u)) if (!niveis.containsKey(a.destino)) {
                niveis.put(a.destino, niveis.get(u) + 1); pais.put(a.destino, u); fila.add(a.destino);
            }
        }
        List<Integer> ausentes = vertices(); ausentes.removeAll(niveis.keySet());
        return new ResultadoBfs(ordem, niveis, ausentes, montarRota(origem, destino, pais, niveis.containsKey(destino)));
    }
    public List<Integer> dfs(int origem) {
        exigir(origem); List<Integer> ordem = new ArrayList<>(); Set<Integer> vistos = new HashSet<>();
        dfs(origem, vistos, ordem); return ordem;
    }
    private void dfs(int u, Set<Integer> vistos, List<Integer> ordem) {
        vistos.add(u); ordem.add(u);
        for (Aresta a : adj.get(u)) if (!vistos.contains(a.destino)) dfs(a.destino, vistos, ordem);
    }
    public boolean temCicloDirecionado() {
        Map<Integer, Integer> cores = new HashMap<>();
        for (int u : vertices()) if (!cores.containsKey(u) && ciclo(u, cores)) return true;
        return false;
    }
    private boolean ciclo(int u, Map<Integer, Integer> cores) {
        cores.put(u, 1); // Cinza: está na pilha de recursão.
        for (Aresta a : adj.get(u)) {
            if (Integer.valueOf(1).equals(cores.get(a.destino))) return true;
            if (!cores.containsKey(a.destino) && ciclo(a.destino, cores)) return true;
        }
        cores.put(u, 2); return false;
    }
    private static final class Estado {
        final int id; final double custo;
        Estado(int id, double custo) { this.id = id; this.custo = custo; }
    }
    public Rota dijkstra(int origem, int destino, Criterio criterio) {
        exigir(origem); exigir(destino);
        if (criterio == null || criterio == Criterio.CONEXOES) throw new IllegalArgumentException("Escolha distância ou tempo.");
        Map<Integer, Double> custos = new HashMap<>(); Map<Integer, Integer> pais = new HashMap<>();
        PriorityQueue<Estado> fila = new PriorityQueue<>(Comparator.comparingDouble((Estado e) -> e.custo).thenComparingInt(e -> e.id));
        custos.put(origem, 0.0); fila.add(new Estado(origem, 0));
        while (!fila.isEmpty()) {
            Estado atual = fila.remove();
            if (atual.custo > custos.getOrDefault(atual.id, Double.POSITIVE_INFINITY)) continue;
            if (atual.id == destino) break;
            for (Aresta a : adj.get(atual.id)) {
                double candidato = atual.custo + (criterio == Criterio.DISTANCIA ? a.distancia : a.tempo);
                if (candidato < custos.getOrDefault(a.destino, Double.POSITIVE_INFINITY)) {
                    custos.put(a.destino, candidato); pais.put(a.destino, atual.id); fila.add(new Estado(a.destino, candidato));
                }
            }
        }
        return montarRota(origem, destino, pais, custos.containsKey(destino));
    }
    private Rota montarRota(int origem, int destino, Map<Integer, Integer> pais, boolean alcancado) {
        if (!alcancado) return new Rota(Collections.<Integer>emptyList(), 0, 0);
        List<Integer> caminho = new ArrayList<>(); int atual = destino; caminho.add(atual);
        while (atual != origem) { atual = pais.get(atual); caminho.add(atual); }
        Collections.reverse(caminho); double distancia = 0, tempo = 0;
        for (int i = 1; i < caminho.size(); i++) {
            Aresta a = aresta(caminho.get(i - 1), caminho.get(i)); distancia += a.distancia; tempo += a.tempo;
        }
        return new Rota(caminho, distancia, tempo);
    }
    /** Componentes fracas: ignora a direção exclusivamente nesta análise. */
    public List<List<Integer>> componentesNaoDirecionadas() {
        Map<Integer, Set<Integer>> projecao = new HashMap<>();
        for (int id : vertices()) projecao.put(id, new HashSet<Integer>());
        for (Aresta a : arestas()) { projecao.get(a.origem).add(a.destino); projecao.get(a.destino).add(a.origem); }
        List<List<Integer>> componentes = new ArrayList<>(); Set<Integer> vistos = new HashSet<>();
        for (int inicio : vertices()) if (vistos.add(inicio)) {
            List<Integer> componente = new ArrayList<>(); Queue<Integer> fila = new ArrayDeque<>(); fila.add(inicio);
            while (!fila.isEmpty()) { int u = fila.remove(); componente.add(u); for (int v : projecao.get(u)) if (vistos.add(v)) fila.add(v); }
            Collections.sort(componente); componentes.add(componente);
        }
        return componentes;
    }
    public boolean conexoNaoDirecionado() { return !adj.isEmpty() && componentesNaoDirecionadas().size() == 1; }
}
