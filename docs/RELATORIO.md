# Relatório técnico e divisão de responsabilidades

**Sistema:** SmartCity — Gerenciamento de Localidades, Rotas e Infraestrutura<br/>**Disciplina:** Estrutura de Dados II<br/>**Professor:** Vitor Lima<br/>**Integrantes:** Davi da Silva Meireles Veloso e Juan Castro Mendonça de Oliveira

## 1. Objetivo

Integrar uma árvore binária de busca e um grafo em uma aplicação Java orientada a objetos. A árvore organiza as localidades por ID; o grafo representa conexões com distância e tempo. O usuário cadastra localidades, consulta caminhos e analisa a rede por um menu de terminal.

## 2. Implementação entregue

O cadastro usa `Localidade`, `NoArvore` e `ArvoreBinariaBusca`. A ABB oferece inserção, busca com contagem de comparações, atualização sem alterar o ID, remoção nos três casos, extremos, altura, nível, contagem e classificação de nós. Há percursos pré-ordem, em ordem, pós-ordem e por níveis.

O balanceamento é verificado em todos os nós. Quando solicitado, a árvore é reconstruída escolhendo o elemento central de uma lista ordenada e repetindo o procedimento nas duas metades. Não há rotações AVL automáticas. A comparação de armazenamento exibe as posições de um vetor indexado a partir de 1 e o desperdício de uma árvore linear.

O módulo `Grafo` usa `HashMap<Integer, List<Aresta>>` para listas de adjacência implementadas no projeto. Cada conexão possui origem, destino, distância e tempo. Mão dupla cria duas arestas direcionadas equivalentes. As coleções Java servem como auxiliares; não fornecem as estruturas e os algoritmos exigidos prontos.

BFS usa fila e visita os vizinhos em ordem crescente de ID. DFS usa recursão. Ciclos dirigidos são identificados pela presença de aresta para um vértice ainda ativo na recursão. Dijkstra usa relaxamento e fila de prioridade, podendo otimizar distância ou tempo.

As componentes são calculadas na projeção não direcionada, enquanto a alcançabilidade e as rotas preservam a direção das arestas. O menu permite cadastro integrado de vértices, manutenção de arestas, consulta de vizinhos e graus e emissão dos quatro relatórios pedidos.

## 3. Integração e consistência

`SmartCity` centraliza as operações: cadastra a localidade na ABB e cria o vértice; valida origem e destino na ABB antes de executar rotas; remove o vértice e as arestas incidentes ao excluir uma localidade. As rotas consultam os nomes atuais na árvore. Balancear apenas reorganiza os nós e não modifica o grafo.

Entradas inválidas recebem mensagens de erro. Números, opções do menu, confirmação s/n, IDs duplicados, nomes vazios, população negativa, pesos inválidos e vértices ausentes são tratados. O encerramento da entrada também é tratado. Os dados ficam em memória, sem persistência após sair.

## 4. Resultados da base de demonstração

A base fictícia possui 8 localidades, população total de 65.000 e 10 arestas direcionadas. A localidade 80 (Ilha) está isolada. A projeção não direcionada apresenta 2 componentes.

| Critério entre 10 e 40 | Caminho | Arestas | km | min |
|---|---|---:|---:|---:|
| BFS | 10 → 40 | 1 | 20 | 30 |
| Dijkstra por distância | 10 → 20 → 30 → 40 | 3 | 6 | 36 |
| Dijkstra por tempo | 10 → 50 → 40 | 2 | 16 | 6 |

A comparação demonstra que minimizar a quantidade de conexões não equivale a minimizar distância ou tempo. Para 8 nós, a ABB inicial e sua reconstrução balanceada usam 15 posições úteis no vetor, com 7 vazias. Uma cadeia à direita usa 255 posições úteis, com 247 vazias; o índice 0 é reservado e não entra nesses totais.

## 5. Validação executada na preparação

A suíte automatizada realizou **2.243 verificações** com sucesso. Ela inclui os percursos e as remoções da ABB, reconstrução, erros de cadastro, operações atômicas de mão dupla, direção das arestas, pesos zero, ciclos, vértices isolados e preservação do grafo após balanceamento.

Como verificação independente de rotas, 12 grafos pseudoaleatórios de 7 vértices foram comparados a Floyd-Warshall para distância, tempo e custo unitário. A árvore também foi submetida a 400 passos pseudoaleatórios de inserção e remoção, comparando a ordem e a contagem com um conjunto de referência.

Esses testes foram executados durante a preparação automatizada do material. A validação pessoal dos integrantes e o ensaio da apresentação ainda precisam ser realizados por eles.

## 6. Divisão entre Davi e Juan

**Natureza deste registro:** divisão proposta de responsabilidades para estudo, revisão, validação e apresentação. O código, os testes e a primeira versão dos materiais foram gerados com assistência de IA. Este texto não declara que os integrantes já executaram pessoalmente atividades que ainda não foram confirmadas.

### Davi da Silva Meireles Veloso — árvore e cadastro

Davi fica responsável por revisar `Localidade`, `NoArvore` e `ArvoreBinariaBusca`; estudar a regra de ordenação por ID e explicar a busca e a remoção de folha, de nó com um filho e de nó com dois filhos. Cabe a Davi validar os quatro percursos, a altura, os níveis, a identificação de folhas e nós internos, a reconstrução balanceada e a comparação entre armazenamento encadeado e vetor.

Na apresentação, Davi conduz a contextualização, a arquitetura e o módulo de árvore. Sua demonstração inclui pesquisar um ID, mostrar os percursos e explicar o desperdício de posições de uma árvore desbalanceada.

**Evidências a registrar após realizar:** execução dos casos de remoção, explicação do sucessor em ordem e conferência dos resultados de percursos e balanceamento. Situação: atividades atribuídas, execução pessoal não confirmada.

### Juan Castro Mendonça de Oliveira — grafo e rotas

Juan fica responsável por revisar `Grafo`; estudar listas de adjacência, direção, pesos e graus e explicar BFS, DFS, detecção de ciclos e Dijkstra. Cabe a Juan validar rotas por distância e tempo, vértices inalcançáveis, componentes da projeção não direcionada e operações de arestas em um ou dois sentidos.

Na apresentação, Juan conduz o módulo de conexões e os algoritmos. Sua demonstração compara as três rotas entre 10 e 40 e mostra por que a localidade 80 não é alcançada.

**Evidências a registrar após realizar:** comparação manual das três rotas, descrição da fila e da recursão e explicação do relaxamento do Dijkstra. Situação: atividades atribuídas, execução pessoal não confirmada.

### Atividades dos dois — integração e entrega

Davi e Juan ficam responsáveis por revisar `SmartCity` e `Main`, executar a suíte e o roteiro manual, confirmar a consistência após atualização, remoção e balanceamento, revisar o relatório e ensaiar a apresentação completa. Os dois devem saber executar o projeto e responder perguntas básicas sobre ambos os módulos.

**Evidências a registrar após realizar:** saída dos testes executados pela dupla, revisão do roteiro e demonstração de exclusão de uma localidade com suas conexões. Situação: atividades atribuídas, execução pessoal não confirmada.

## 7. Limitações e decisões

A aplicação é didática e mantém dados apenas em memória. Não usa balanceamento automático, banco de dados ou interface gráfica. Árvores e DFS usam recursão em parte das operações, portanto sua profundidade prática depende da pilha disponível. O vetor real possui limite de alocação; índices lógicos grandes são apresentados sem alocar lacunas. Pesos e somas devem permanecer na faixa representável de `double`.

Uma conexão de mão dupla equivale a duas arestas dirigidas. Assim, ela forma um ciclo dirigido de comprimento 2. Ao alterar pesos nos dois sentidos, a aplicação conserva sua equivalência. O grafo vazio é considerado não conexo; altura é medida em arestas, com vazia = -1 e folha = 0.

## 8. Fonte e reprodução

Requisitos extraídos da apresentação “Projeto Final (2).pptx”, fornecida pela turma, professor Vitor Lima. Para reproduzir a validação, executar `powershell -ExecutionPolicy Bypass -File .\executar.ps1 testes`. Para obter os resultados de demonstração, usar o argumento `demo`. O guia de apresentação contém falas sugeridas, perguntas e passos de demonstração.
