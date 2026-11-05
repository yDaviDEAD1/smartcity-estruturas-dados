package smartcity;

import java.math.BigInteger;
import java.util.*;

/** Testes sem dependências, incluindo oráculos independentes e regressões de integração. */
public final class Testes {
    private static int verificacoes;
    private static void verificar(boolean ok, String caso) { verificacoes++; if (!ok) throw new AssertionError(caso); }
    private static void rejeitar(Runnable acao, String caso) {
        try { acao.run(); } catch (IllegalArgumentException e) { verificacoes++; return; }
        throw new AssertionError("Deveria rejeitar: " + caso);
    }
    private static Localidade local(int id) { return new Localidade(id, "Local " + id, id * 10L); }
    private static List<Integer> ids(List<Localidade> lista) { List<Integer> r = new ArrayList<>(); for (Localidade l : lista) r.add(l.getId()); return r; }
    private static boolean perto(double a, double b) { return Math.abs(a - b) < 1e-8; }
    public static void executar() {
        verificacoes = 0; testarArvore(); testarGrafo(); testarIntegracao(); testarAleatorios();
        System.out.println("TESTES OK: " + verificacoes + " verificações passaram. Dados do menu não foram alterados.");
    }
    private static void testarArvore() {
        ArvoreBinariaBusca a = new ArvoreBinariaBusca();
        verificar(a.vazia() && a.altura() == -1 && a.balanceada(), "árvore vazia");
        verificar(a.menor() == null && a.maior() == null && a.nivel(5) == -1, "consultas vazias");
        verificar(a.vetorPorNiveis(0).length == 1, "vetor vazio com índice reservado");
        for (int id : new int[]{40,20,60,10,30,50,70}) a.inserir(local(id));
        verificar(ids(a.emOrdem()).equals(Arrays.asList(10,20,30,40,50,60,70)), "em ordem");
        verificar(ids(a.preOrdem()).equals(Arrays.asList(40,20,10,30,60,50,70)), "pré-ordem");
        verificar(ids(a.posOrdem()).equals(Arrays.asList(10,30,20,50,70,60,40)), "pós-ordem");
        verificar(ids(a.porNiveis()).equals(Arrays.asList(40,20,60,10,30,50,70)), "níveis");
        verificar(a.altura() == 2 && a.nivel(70) == 2 && a.quantidade() == 7 && a.balanceada(), "métricas");
        verificar(a.folhas().size() == 4 && a.internos().size() == 3, "classificação de nós");
        verificar(a.buscarComComparacoes(70).comparacoes == 3, "comparações");
        verificar(a.menor().getId() == 10 && a.maior().getId() == 70, "extremos");
        verificar(a.vetorPorNiveis(7)[7].getId() == 70, "vetor real");
        rejeitar(() -> a.inserir(local(40)), "ID duplicado");
        a.atualizar(new Localidade(20, "Novo", 99)); verificar(a.buscar(20).getNome().equals("Novo"), "atualizar");
        a.remover(10); verificar(a.buscar(10) == null && a.quantidade() == 6, "remover folha");
        a.remover(20); verificar(a.buscar(20) == null && a.buscar(30) != null, "remover um filho");
        a.remover(40); verificar(a.buscar(40) == null && a.quantidade() == 4, "remover dois filhos/raiz");
        verificar(ids(a.emOrdem()).equals(Arrays.asList(30,50,60,70)), "ordem após remoção");
        rejeitar(() -> a.remover(999), "remoção ausente");
        ArvoreBinariaBusca cadeia = new ArvoreBinariaBusca(); for (int i = 1; i <= 10; i++) cadeia.inserir(local(i));
        verificar(!cadeia.balanceada() && cadeia.altura() == 9, "cadeia desbalanceada");
        verificar(cadeia.posicoesVetor().containsKey(BigInteger.valueOf(1023)), "índice exponencial");
        rejeitar(() -> cadeia.vetorPorNiveis(100), "limite do vetor");
        cadeia.balancear(); verificar(cadeia.balanceada() && cadeia.altura() == 3 && cadeia.quantidade() == 10, "reconstrução");
        ArvoreBinariaBusca interior = new ArvoreBinariaBusca();
        for (int i : new int[]{50,30,70,20,10,60,80,90}) interior.inserir(local(i));
        verificar(interior.alturaEsquerda() == interior.alturaDireita() && !interior.balanceada(), "verificar balanceamento além da raiz");
        for (Localidade l : new ArrayList<>(a.emOrdem())) a.remover(l.getId()); verificar(a.vazia(), "remover tudo");
        rejeitar(() -> new Localidade(0,"x",1), "ID inválido"); rejeitar(() -> new Localidade(1," ",1), "nome vazio"); rejeitar(() -> new Localidade(1,"x",-1), "população negativa");
    }
    private static void testarGrafo() {
        Grafo g = new Grafo(); verificar(!g.conexoNaoDirecionado() && !g.temCicloDirecionado(), "rede vazia");
        for (int id = 1; id <= 5; id++) g.adicionarVertice(id);
        g.adicionarAresta(1,3,8,2,false); g.adicionarAresta(1,2,1,8,false); g.adicionarAresta(2,3,1,8,false); g.adicionarAresta(3,4,1,1,false);
        verificar(g.bfs(1,4).ordem.equals(Arrays.asList(1,2,3,4)), "BFS crescente");
        verificar(g.bfs(1,4).rota.caminho.equals(Arrays.asList(1,3,4)), "BFS minimiza arestas");
        verificar(g.bfs(1,4).niveis.get(4) == 2 && g.bfs(1,4).naoAlcancados.equals(Arrays.asList(5)), "níveis e não alcançados");
        verificar(g.dfs(1).equals(Arrays.asList(1,2,3,4)), "DFS");
        verificar(!g.temCicloDirecionado(), "DAG com aresta cruzada não é ciclo");
        verificar(g.dijkstra(1,4,Grafo.Criterio.DISTANCIA).caminho.equals(Arrays.asList(1,2,3,4)), "distância");
        verificar(g.dijkstra(1,4,Grafo.Criterio.TEMPO).caminho.equals(Arrays.asList(1,3,4)), "tempo");
        verificar(!g.bfs(4,1).rota.encontrada() && !g.dijkstra(1,5,Grafo.Criterio.TEMPO).encontrada(), "direção e inalcançável");
        verificar(g.dijkstra(1,1,Grafo.Criterio.TEMPO).conexoes() == 0 && g.bfs(1,1).rota.encontrada(), "origem igual destino");
        verificar(g.isolados().equals(Arrays.asList(5)) && g.componentesNaoDirecionadas().size() == 2, "componentes e isolados");
        verificar(g.grauEntrada(3) == 2 && g.grauSaida(1) == 2, "graus");
        rejeitar(() -> g.adicionarAresta(1,2,1,1,false), "aresta duplicada");
        rejeitar(() -> g.adicionarAresta(1,99,1,1,false), "destino inexistente");
        rejeitar(() -> g.adicionarAresta(4,5,-1,1,false), "peso negativo");
        rejeitar(() -> g.adicionarAresta(4,5,Double.NaN,1,false), "NaN");
        rejeitar(() -> g.adicionarAresta(4,5,1,Double.POSITIVE_INFINITY,false), "infinito");
        rejeitar(() -> g.adicionarAresta(4,4,1,1,false), "laço");
        rejeitar(() -> g.adicionarAresta(2,1,1,1,true), "conflito bidirecional"); verificar(!g.adjacentes(2,1), "atomicidade inserir");
        rejeitar(() -> g.alterarPesos(1,2,4,4,true), "reversa ausente"); verificar(g.aresta(1,2).distancia == 1, "atomicidade alterar");
        g.adicionarAresta(4,5,0,0,true); verificar(g.temCicloDirecionado(), "duas arestas reversas formam ciclo dirigido");
        g.alterarPesos(4,5,3,4,true); verificar(g.aresta(5,4).tempo == 4, "alterar dupla");
        g.removerAresta(4,5,true); verificar(!g.adjacentes(5,4), "remover dupla");
        g.removerVertice(3); verificar(g.arestas().size() == 1 && g.grauEntrada(4) == 0, "remover incidentes");
    }
    private static void testarIntegracao() {
        SmartCity s = SmartCity.demonstracao(); verificar(s.consistente(), "base consistente");
        Grafo.Rota bfs = s.rota(10,40,Grafo.Criterio.CONEXOES), km = s.rota(10,40,Grafo.Criterio.DISTANCIA), min = s.rota(10,40,Grafo.Criterio.TEMPO);
        verificar(bfs.caminho.equals(Arrays.asList(10,40)) && perto(bfs.distancia,20) && perto(bfs.tempo,30), "rota demonstração BFS");
        verificar(km.caminho.equals(Arrays.asList(10,20,30,40)) && perto(km.distancia,6) && perto(km.tempo,36), "rota demonstração km");
        verificar(min.caminho.equals(Arrays.asList(10,50,40)) && perto(min.distancia,16) && perto(min.tempo,6), "rota demonstração tempo");
        s.atualizar(40,"Jardim Atualizado",9000); verificar(s.descreverRota(km).contains("Jardim Atualizado"), "nome atualizado na rota");
        String conexoes = s.conexoes(); s.balancear(); verificar(s.consistente() && s.conexoes().equals(conexoes), "balancear preserva grafo");
        s.remover(20); verificar(s.consistente() && !s.rota(10,40,Grafo.Criterio.DISTANCIA).caminho.contains(20), "remoção integrada");
        rejeitar(() -> s.conectar(999,40,1,1,false), "validação pela árvore");
        rejeitar(() -> s.cadastrar(10,"Duplicado",1), "duplicação integrada"); verificar(s.consistente(), "consistência após erros");
        SmartCity vazio = new SmartCity(); verificar(vazio.relatorioCadastro().contains("Total: 0") && vazio.consistente(), "relatórios vazios");
    }
    private static void testarAleatorios() {
        Random random = new Random(42);
        // Compara Dijkstra a Floyd-Warshall e BFS a uma matriz de custo unitário.
        for (int rodada = 0; rodada < 12; rodada++) {
            int n = 7; Grafo g = new Grafo(); double[][] km = new double[n][n], min = new double[n][n], saltos = new double[n][n];
            for (int i = 0; i < n; i++) {
                g.adicionarVertice(i+1);
                for (int j = 0; j < n; j++) { km[i][j] = min[i][j] = saltos[i][j] = i == j ? 0 : Double.POSITIVE_INFINITY; }
            }
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (i != j && random.nextDouble() < .3) {
                int d = random.nextInt(20), t = random.nextInt(20); g.adicionarAresta(i+1,j+1,d,t,false); km[i][j] = d; min[i][j] = t; saltos[i][j] = 1;
            }
            for (double[][] matriz : Arrays.asList(km,min,saltos)) for (int k = 0; k < n; k++) for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) matriz[i][j] = Math.min(matriz[i][j],matriz[i][k]+matriz[k][j]);
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
                Grafo.Rota d = g.dijkstra(i+1,j+1,Grafo.Criterio.DISTANCIA), t = g.dijkstra(i+1,j+1,Grafo.Criterio.TEMPO), b = g.bfs(i+1,j+1).rota;
                verificar(d.encontrada() == Double.isFinite(km[i][j]) && (!d.encontrada() || perto(d.distancia,km[i][j])), "oráculo Dijkstra km");
                verificar(t.encontrada() == Double.isFinite(min[i][j]) && (!t.encontrada() || perto(t.tempo,min[i][j])), "oráculo Dijkstra min");
                verificar(b.encontrada() == Double.isFinite(saltos[i][j]) && (!b.encontrada() || b.conexoes() == saltos[i][j]), "oráculo BFS");
            }
        }
        ArvoreBinariaBusca a = new ArvoreBinariaBusca(); Set<Integer> esperado = new HashSet<>();
        for (int passo = 0; passo < 400; passo++) {
            int id = 1 + random.nextInt(80);
            if (random.nextBoolean()) { if (esperado.add(id)) a.inserir(local(id)); }
            else if (esperado.remove(id)) a.remover(id);
            List<Integer> ordenados = new ArrayList<>(esperado); Collections.sort(ordenados);
            verificar(ids(a.emOrdem()).equals(ordenados) && a.quantidade() == esperado.size(), "ABB versus conjunto ordenado");
            if (passo % 30 == 0) { a.balancear(); verificar(a.balanceada(), "balancear após mutações"); }
        }
    }
}
