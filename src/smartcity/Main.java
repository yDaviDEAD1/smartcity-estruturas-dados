package smartcity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Main {
    private final BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final SmartCity sistema = SmartCity.demonstracao();
    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        if (args.length > 0 && args[0].equals("--testes")) { Testes.executar(); return; }
        if (args.length > 0 && args[0].equals("--demo")) {
            SmartCity s = SmartCity.demonstracao();
            System.out.println(s.relatorioCadastro()); System.out.println(s.relatorioArvore(10, 80, 999));
            System.out.println(s.relatorioRede()); System.out.println(s.compararRotas(10, 40)); System.out.println(s.armazenamento()); return;
        }
        new Main().executar();
    }
    private void executar() {
        System.out.println("SMARTCITY — Estrutura de Dados II\nDavi da Silva Meireles Veloso | Juan Castro Mendonça de Oliveira\nDados fictícios de demonstração carregados. Alterações ficam em memória.");
        while (true) {
            try {
                System.out.println("\n1 Cadastrar localidade   2 Pesquisar   3 Atualizar   4 Remover\n5 Visualizar árvore   6 Percursos   7 Balancear   8 Conexões\n9 BFS   10 DFS   11 Dijkstra   12 Conectividade\n13 Comparar rotas   14 Relatórios   15 Testes   16 Armazenamento\n0 Sair");
                int opcao = inteiro("Opção: ");
                switch (opcao) {
                    case 0: System.out.println("Até logo!"); return;
                    case 1: sistema.cadastrar(inteiro("ID positivo: "), texto("Nome: "), longo("População: ")); sucesso(); break;
                    case 2: {
                        ArvoreBinariaBusca.Busca b = sistema.busca(inteiro("ID: "));
                        System.out.println((b.localidade == null ? "Não encontrada." : b.localidade) + " | comparações: " + b.comparacoes); break;
                    }
                    case 3: { int id = inteiro("ID a atualizar (será preservado): "); sistema.consultar(id); sistema.atualizar(id, texto("Novo nome: "), longo("Nova população: ")); sucesso(); break; }
                    case 4: { int id = inteiro("ID a remover: "); System.out.println(sistema.consultar(id)); if (sim("Remover localidade e todas as suas conexões? (s/n): ")) { sistema.remover(id); sucesso(); } else System.out.println("Cancelado."); break; }
                    case 5: System.out.println(sistema.relatorioArvore()); System.out.println(sistema.armazenamento()); break;
                    case 6: System.out.println(sistema.percursos()); break;
                    case 7: sistema.balancear(); sucesso(); System.out.println(sistema.relatorioArvore()); break;
                    case 8: gerenciarConexoes(); break;
                    case 9: {
                        Grafo.ResultadoBfs b = sistema.bfs(inteiro("Origem: "), inteiro("Destino: "));
                        System.out.println("Ordem BFS: " + b.ordem + "\nNíveis: " + b.niveis + "\nVisitados: " + b.ordem.size() + "\nNão alcançados: " + b.naoAlcancados + "\n" + sistema.descreverRota(b.rota)); break;
                    }
                    case 10: System.out.println("DFS: " + sistema.dfs(inteiro("Origem: "))); System.out.println(sistema.relatorioRede()); break;
                    case 11: {
                        int origem = inteiro("Origem: "), destino = inteiro("Destino: "), criterio = inteiro("1 Distância | 2 Tempo: ");
                        if (criterio != 1 && criterio != 2) throw new IllegalArgumentException("Critério inválido.");
                        System.out.println(sistema.descreverRota(sistema.rota(origem, destino, criterio == 1 ? Grafo.Criterio.DISTANCIA : Grafo.Criterio.TEMPO))); break;
                    }
                    case 12: {
                        System.out.println(sistema.relatorioRede()); int origem = inteiro("Origem para testar alcançabilidade: "), destino = inteiro("Destino: ");
                        System.out.println("Alcançável respeitando direção: " + sistema.bfs(origem, destino).rota.encontrada()); break;
                    }
                    case 13: System.out.println(sistema.compararRotas(inteiro("Origem: "), inteiro("Destino: "))); break;
                    case 14: {
                        System.out.println(sistema.relatorioCadastro()); System.out.println(sistema.relatorioArvore(inteiro("ID para análise de busca: ")));
                        System.out.println(sistema.relatorioRede()); System.out.println(sistema.compararRotas(inteiro("Origem do relatório D: "), inteiro("Destino: "))); break;
                    }
                    case 15: Testes.executar(); break;
                    case 16: System.out.println(sistema.armazenamento()); break;
                    default: System.out.println("Opção inexistente. Escolha uma opção do menu.");
                }
            } catch (EOFException e) { System.out.println("\nEntrada encerrada. Até logo!"); return; }
            catch (IllegalArgumentException e) { System.out.println("Erro: " + e.getMessage()); }
            catch (IOException e) { System.out.println("Falha ao ler entrada: " + e.getMessage()); return; }
        }
    }
    private void gerenciarConexoes() throws IOException {
        System.out.println("1 Adicionar | 2 Remover | 3 Alterar pesos | 4 Listar | 5 Vizinhos, graus e adjacência | 0 Voltar");
        int op = inteiro("Opção: "); if (op == 0) return;
        if (op == 4) { System.out.println(sistema.conexoes()); return; }
        if (op < 1 || op > 5) throw new IllegalArgumentException("Opção inválida.");
        int origem = inteiro("Origem: "), destino = inteiro("Destino: ");
        if (op == 5) { System.out.println(sistema.vizinhanca(origem, destino)); return; }
        double km = 0, min = 0;
        if (op != 2) { km = decimal("Distância em km: "); min = decimal("Tempo em minutos: "); }
        boolean dupla = sim("Aplicar nos dois sentidos? (s/n): ");
        if (op == 1) sistema.conectar(origem, destino, km, min, dupla);
        if (op == 2) sistema.desconectar(origem, destino, dupla);
        if (op == 3) sistema.alterarPesos(origem, destino, km, min, dupla);
        sucesso();
    }
    private void sucesso() { System.out.println("Operação realizada. Consistência entre árvore e grafo: " + sistema.consistente()); }
    private String texto(String mensagem) throws IOException { System.out.print(mensagem); String s = entrada.readLine(); if (s == null) throw new EOFException(); return s.trim(); }
    private int inteiro(String mensagem) throws IOException {
        while (true) { try { return Integer.parseInt(texto(mensagem)); } catch (NumberFormatException e) { System.out.println("Digite um inteiro válido (até 2147483647)."); } }
    }
    private long longo(String mensagem) throws IOException {
        while (true) { try { return Long.parseLong(texto(mensagem)); } catch (NumberFormatException e) { System.out.println("Digite uma população inteira válida."); } }
    }
    private double decimal(String mensagem) throws IOException {
        while (true) {
            try { double n = Double.parseDouble(texto(mensagem).replace(',', '.')); if (!Double.isFinite(n)) throw new NumberFormatException(); return n; }
            catch (NumberFormatException e) { System.out.println("Digite um número finito, como 2,5."); }
        }
    }
    private boolean sim(String mensagem) throws IOException {
        while (true) { String r = texto(mensagem).toLowerCase(Locale.ROOT); if (r.equals("s")) return true; if (r.equals("n")) return false; System.out.println("Responda s ou n."); }
    }
}
