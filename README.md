# SmartCity — Árvores e Grafos

Projeto de **Estrutura de Dados II**, professor **Vitor Lima**.

Integrantes: **Davi da Silva Meireles Veloso** e **Juan Castro Mendonça de Oliveira**.

Aplicação Java com menu no terminal, ABB encadeada própria, grafo próprio por listas de adjacência e algoritmos implementados no código. Não usa bibliotecas de grafos ou árvores prontas. Código e materiais iniciais foram produzidos com assistência de IA; a divisão de revisão e apresentação está em [docs/RELATORIO.md](docs/RELATORIO.md).

## Executar

Instale um **JDK 9 ou superior** e tenha `javac` e `java` no PATH. Compilação validada com JDK 21 e execução com Java 8; `--release 8` mantém compatibilidade do bytecode.

Windows, na pasta do projeto:

```powershell
powershell -ExecutionPolicy Bypass -File .\executar.ps1
powershell -ExecutionPolicy Bypass -File .\executar.ps1 testes
powershell -ExecutionPolicy Bypass -File .\executar.ps1 demo
```

Linux/macOS:

```sh
sh executar.sh
sh executar.sh testes
sh executar.sh demo
```

Ou compile sem scripts:

```sh
mkdir -p build/classes
javac --release 8 -encoding UTF-8 -d build/classes src/smartcity/*.java
java -Dfile.encoding=UTF-8 -cp build/classes smartcity.Main
```

Os dados são fictícios, carregados a cada execução. Não há persistência em disco, pois o enunciado não exige. Ao sair, alterações são descartadas. Arquivos Java usam UTF-8.

## Demonstração principal

No menu, escolha **13**, origem **10**, destino **40**:

| Critério | Caminho | Arestas | Distância | Tempo |
|---|---|---:|---:|---:|
| BFS: menos conexões | 10 → 40 | 1 | 20 km | 30 min |
| Dijkstra: distância | 10 → 20 → 30 → 40 | 3 | 6 km | 36 min |
| Dijkstra: tempo | 10 → 50 → 40 | 2 | 16 km | 6 min |

A localidade **80 — Ilha** está isolada para demonstrar destinos inalcançáveis. A base tem 8 localidades, população total de 65.000, 10 arestas direcionadas e 2 componentes na projeção não direcionada.

## Organização

| Arquivo | Responsabilidade |
|---|---|
| `Localidade.java` | ID imutável, nome e população validados |
| `NoArvore.java` | Nó com referências esquerda e direita |
| `ArvoreBinariaBusca.java` | CRUD, métricas, percursos, balanceamento e vetor |
| `Grafo.java` | Listas de adjacência, BFS, DFS, ciclos, Dijkstra e componentes |
| `SmartCity.java` | Integração, validação dos IDs e relatórios A–D |
| `Main.java` | Menu, tratamento de entrada e demonstração |
| `Testes.java` | Regressões e comparação com resultados de referência |

## Requisitos e onde demonstrar

| Requisito do enunciado | Menu / implementação |
|---|---|
| Cadastrar, buscar, atualizar e remover localidades | 1–4; criação/remoção sincronizam vértices |
| Remoção de folha, um filho e dois filhos | `ArvoreBinariaBusca.remover`; regressões em `Testes` |
| Menor/maior ID, contagem, folhas, internos, altura e nível | 5 e 14; informar ID na análise de busca |
| Árvore vazia | Relatório B e teste após remover todos os nós |
| Pré-ordem, em ordem, pós-ordem e níveis | 6 |
| Balanceamento em cada nó e reconstrução sem AVL | 5 e 7 |
| Vetor com índices a partir de 1 e desperdício de memória | 16; representação real limitada e índices lógicos exatos |
| Inserção/remoção de vértices | 1 e 4, via cadastro integrado |
| Adicionar/remover aresta e alterar pesos | 8, opções 1–3 |
| Vértices, arestas, vizinhos, graus e adjacência | 8, opções 4–5 |
| Duplicatas e vértices inexistentes | Rejeitados antes de modificar a rede |
| Mão única e mão dupla | 8; mão dupla cria duas arestas de pesos iguais |
| BFS com fila, níveis, ordem, ausentes e rota | 9; vizinhos em ID crescente |
| DFS, alcançáveis e ciclo dirigido | 10; cores distinguem recursão ativa e finalizada |
| Dijkstra por distância e por tempo | 11; fila de prioridade é apenas coleção auxiliar |
| Conectividade e componentes | 12; componentes na projeção não direcionada |
| Comparação de três rotas | 13 |
| Relatórios A, B, C e D | 14 |
| Testes integrados | 15 ou `executar.ps1 testes` |
| Entrada inválida e fim de entrada | `Main`: validação numérica, opções, s/n e EOF |

## Convenções

- ID positivo, nome não vazio e população não negativa. Atualizar preserva ID.
- Altura em arestas: árvore vazia = -1, folha = 0; raiz no nível 0, ID inexistente = -1.
- Uma comparação de busca significa uma comparação de chave por nó visitado, inclusive em busca sem sucesso.
- Distância e tempo são `double`, finitos e não negativos; pesos zero são aceitos. Laços de uma localidade para ela mesma são rejeitados por regra da aplicação.
- Não existem arestas paralelas para o mesmo par ordenado. Operações de mão dupla validam ambos os sentidos antes de modificar qualquer um.
- As duas arestas de mão dupla são armazenadas como arestas dirigidas independentes. Para manter pesos equivalentes em uma alteração, escolha aplicar nos dois sentidos; alterar só um sentido passa a representar duas conexões dirigidas diferentes.
- Em grafo dirigido, A → B → A é ciclo, mesmo quando resulta de uma conexão de mão dupla. Componentes ignoram direção; rotas e alcançabilidade respeitam direção.
- Grafo vazio tem zero componentes e é considerado não conexo nesta aplicação.
- Grau total é entrada + saída; uma conexão de mão dupla contribui duas unidades por extremidade.
- Empates no Dijkstra: prioridade por custo e ID; mantém o primeiro predecessor de custo estritamente menor. Não promete menos arestas entre rotas de mesmo peso.
- O vetor reserva índice 0 e usa `2*i`, `2*i+1`, `i/2`. A alocação real é limitada a 4.095 posições úteis no menu; além disso, exibe índices lógicos `BigInteger` sem alocar lacunas. O limite evita esgotar memória em árvores lineares.
- Os algoritmos recursivos usam pilha proporcional à altura/profundidade. Aplicação didática em memória, sem pretensão de processar redes ou cadeias de tamanho arbitrário. Pesos e somas devem permanecer na faixa representável de `double`.

## Complexidade

Considere N localidades, V vértices, E arestas e h altura da árvore.

| Operação | Tempo | Espaço auxiliar |
|---|---|---|
| Busca, inserção e remoção ABB | O(h): O(log N) balanceada, O(N) linear | Busca/inserção O(1); remoção O(h) |
| Percursos e checagem de balanceamento | O(N) | O(h) recursivo; BFS O(N) |
| Reconstruir balanceada | O(N) | O(N) |
| BFS e DFS: travessia | O(V + E) | O(V) |
| Detectar ciclos | O(V + E), mais ordenação dos IDs | O(V) |
| Dijkstra com entradas repetidas no heap | O((V + E) log(V + E)) | O(V + E) |
| Componentes não direcionadas | O(V + E), mais ordenação das saídas | O(V + E) |
| Inserir uma aresta com lista ordenada | O(grau de saída) por sentido | O(1) além da nova aresta |

Os métodos públicos BFS/DFS também ordenam a lista completa de IDs quando necessário, acrescentando O(V log V); a reconstrução da rota consulta arestas nas listas e pode custar O(E). O armazenamento mantém vizinhos ordenados na inserção, sem ordenar novamente a cada visita. A fila de prioridade não fornece Dijkstra pronto: relaxamento, distâncias, predecessores e reconstrução estão implementados em `Grafo`.

## Materiais de estudo

- [Guia e roteiro de apresentação](docs/GUIA_APRESENTACAO.md)
- [Relatório técnico e divisão de responsabilidades](docs/RELATORIO.md)
- `docs/Apresentacao_SmartCity.pptx`: slides editáveis com notas para estudo.
- `docs/Relatorio_SmartCity.pdf`: versão para leitura e impressão.

Fonte dos requisitos: apresentação “Projeto Final (2).pptx”, fornecida pela turma, professor Vitor Lima. O arquivo original não é redistribuído neste repositório.
