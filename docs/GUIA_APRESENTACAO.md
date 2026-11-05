# Guia de estudo e apresentação — SmartCity

Integrantes: Davi da Silva Meireles Veloso e Juan Castro Mendonça de Oliveira. Tempo sugerido: 15 a 20 minutos, incluindo demonstração. O roteiro é uma proposta de fala; leiam o código e executem os exemplos antes da apresentação. A implementação inicial foi gerada com assistência de IA.

## Preparação dos dois

1. Executar `powershell -ExecutionPolicy Bypass -File .\executar.ps1 testes` e conferir o resultado.
2. Rodar `demo` e comparar com os valores deste guia.
3. Ler sua parte do código, mas também acompanhar a fachada `SmartCity`.
4. Ensaiar explicar os algoritmos sem ler os slides. Uma pergunta pode ser dirigida a qualquer integrante.
5. Depois da revisão, atualizar o relatório com as atividades efetivamente feitas por cada um.

## Roteiro dos slides e falas sugeridas

### 1. SmartCity — abertura | Davi | 30 segundos

“O sistema gerencia localidades e conexões de transporte. Ele integra uma árvore binária de busca, que organiza o cadastro por ID, com um grafo, que representa as rotas.” Apresentar os dois integrantes e o objetivo, sem afirmar autoria pessoal de trechos gerados com IA.

### 2. Arquitetura e fluxo | Davi | 1 minuto

“O menu recebe a solicitação e chama a fachada SmartCity. Ela consulta os IDs na árvore antes de chamar o grafo. A árvore é a fonte dos nomes e das populações; o grafo guarda as conexões.” Abrir `SmartCity.rota` e apontar as duas consultas antes do algoritmo.

### 3. Árvore binária de busca | Davi | 1 minuto

“Para cada nó, IDs menores ficam à esquerda e maiores à direita. Essa regra permite descartar uma subárvore a cada comparação. Na base inicial, a raiz é 40; para buscar 10, visitamos 40, 20 e 10: três comparações de chave.”

### 4. Remoção | Davi | 1 minuto

“Uma folha é removida diretamente. Um nó com um filho é substituído pelo filho. Com dois filhos, copiamos o sucessor, o menor da subárvore direita, e removemos sua ocorrência antiga.”

Para demonstrar os três casos, use uma sessão nova para cada exemplo: remover **80** (folha), remover **70** (um filho, 80) e remover **20** (dois filhos, 10 e 30).

### 5. Percursos | Davi | 1 minuto

“Pré-ordem visita a raiz antes dos filhos; em ordem, entre esquerda e direita; pós-ordem, depois; por níveis usa fila.” O em ordem é crescente porque esquerda < raiz < direita em toda a ABB. Demonstrar a opção 6: em ordem, a base lista 10, 20, 30, 40, 50, 60, 70 e 80.

### 6. Balanceamento e armazenamento | Davi | 1 minuto e meio

“Balanceada significa diferença de altura de no máximo um em cada nó, não apenas na raiz. A reconstrução pega o centro da lista ordenada como raiz e repete nas metades. Isso não é AVL: novas inserções ainda podem desbalancear.”

“No vetor, filhos ocupam 2i e 2i+1. Com oito nós, a árvore balanceada do exemplo exige 15 posições úteis. Uma cadeia à direita exige 255, das quais 247 ficam vazias. O encadeamento não precisa representar essas lacunas.” Não dizer que toda árvore balanceada ocupa exatamente N posições: balanceamento não significa árvore completa.

### 7. Grafo e conexões | Juan | 1 minuto

“Cada ID cadastrado corresponde a um vértice. Cada aresta guarda origem, destino, quilômetros e minutos. Mão dupla vira duas arestas direcionadas com pesos equivalentes.” Mostrar que `HashMap` e listas apenas armazenam o grafo construído pelo projeto; não são biblioteca de algoritmos de grafos.

### 8. BFS | Juan | 1 minuto

“A busca em largura usa fila e descobre todos os vértices a uma conexão antes dos que estão a duas. Guardamos nível e predecessor quando o vértice entra na fila. Assim recuperamos o menor caminho em quantidade de arestas.” Na base, BFS a partir de 10 visita **10, 20, 40, 50, 30, 60, 70**. O ID 80 não é visitado. O caminho de 10 a 40 é direto.

### 9. DFS e ciclos | Juan | 1 minuto

“A busca em profundidade segue um ramo até o fim e depois retorna. Na base, a partir de 10: **10, 20, 30, 40, 60, 70, 50**. Para ciclos dirigidos, distinguimos vértices ativos na recursão e já concluídos. Encontrar uma aresta para um ativo indica ciclo.” A conexão 10 ↔ 20 já forma ciclo dirigido; não aplicar a regra de ignorar o pai usada em grafos não direcionados.

### 10. Dijkstra | Juan | 1 minuto e meio

“Começamos com custo zero na origem. A fila de prioridade entrega o menor custo conhecido. Para cada vizinho, somamos o peso da aresta. Se melhorar o custo, atualizamos distância e predecessor e colocamos o novo estado na fila.” Mostrar o trecho `candidato < custos.getOrDefault(...)` e o descarte de estados antigos. Pesos negativos são rejeitados porque invalidam a garantia do algoritmo.

### 11. Três critérios de rota | Juan | 2 minutos com demonstração

Menu **13**, origem **10**, destino **40**:

| Critério | Rota | Conexões | km | min |
|---|---|---:|---:|---:|
| BFS | 10 → 40 | 1 | 20 | 30 |
| Distância | 10 → 20 → 30 → 40 | 3 | 6 | 36 |
| Tempo | 10 → 50 → 40 | 2 | 16 | 6 |

“A rota direta usa menos conexões, mas não é a mais curta nem a mais rápida. Cada algoritmo responde ao objetivo escolhido.”

### 12. Conectividade | Juan | 1 minuto

“As rotas respeitam as setas. Para agrupar a rede em componentes, ignoramos as setas em uma projeção temporária. Isso mede conectividade fraca, não forte.” A base tem duas componentes: [10, 20, 30, 40, 50, 60, 70] e [80]. Não confundir estar na mesma componente não direcionada com conseguir ir e voltar entre qualquer par.

### 13. Integração e relatórios | Davi e Juan | 1 minuto e meio

Davi: “Atualizar nome não muda o ID nem as conexões. O texto das rotas vem da árvore atualizada.” Juan: “Ao remover uma localidade, removemos o vértice e todas as arestas incidentes. Reconstruir a árvore não mexe no grafo.” Listar relatórios de cadastro, árvore, rede e rotas.

### 14. Complexidade | Os dois | 1 minuto

“A busca da ABB depende da altura: O(h). Reconstruir custa O(N). As travessias BFS e DFS custam O(V+E), além da ordenação das saídas em alguns métodos. O Dijkstra usa fila de prioridade com estados repetidos, com limite O((V+E) log(V+E)).” N é a quantidade de localidades, V a de vértices e E a de arestas. Consultar a tabela de complexidade no README para as particularidades do código.

### 15. Validação | Os dois | 1 minuto

“A suíte tem 2.243 verificações. Além de exemplos fixos, os caminhos foram comparados a Floyd-Warshall em grafos pseudoaleatórios; a ABB foi comparada a um conjunto ordenado em sequências de inserção e remoção.” A contagem significa verificações, não 2.243 cenários independentes. Depois que executarem pessoalmente, podem dizer que reproduziram os testes.

### 16. Divisão e decisões | Os dois | 1 minuto

Explicar a divisão de revisão e apresentação: Davi, árvore e cadastro; Juan, grafo e rotas; ambos, integração e validação. A implementação inicial e os materiais foram gerados com assistência de IA. Relatar como concluídas apenas as atividades pessoais que já tiverem feito. Encerrar mostrando que árvore e grafo permanecem consistentes.

## Demonstração manual sem surpresas

Use uma sessão nova, com dados originais. O menu 15 executa testes em instâncias separadas.

1. Opção **2**, ID **10**: Centro; 3 comparações.
2. Opção **6**: mostrar em ordem crescente de 10 a 80.
3. Opção **9**, origem **10**, destino **40**: ordem, níveis, 7 visitados e ausente 80.
4. Opção **13**, origem **10**, destino **40**: tabela com as três rotas acima.
5. Opção **13**, origem **10**, destino **80**: nenhum caminho.
6. Opção **3**, ID **40**, nome **Jardim Atualizado**, população **8000**; repetir comparação para mostrar nome atualizado.
7. Opção **7**; repetir comparação: conexões e custos preservados.
8. Opção **4**, ID **20**, confirmar **s**; usar **8**, depois **4**, e conferir que nenhuma aresta cita 20.
9. Opção **1**, ID **10**, qualquer nome e população: erro de duplicidade; aplicação continua.
10. Digitar **abc** na opção do menu: mensagem de inteiro inválido; digitar **15** para executar testes; **0** para sair.

## Perguntas prováveis e respostas

**Por que não usar só o grafo?** O enunciado pede integrar duas estruturas. O cadastro e a recuperação dos dados por ID ficam na ABB; o grafo trabalha com conexões entre esses IDs.

**Por que não usar `TreeMap`?** Ele substituiria a estrutura central que precisa ser implementada. O projeto usa nós próprios e referências de filhos.

**Busca na árvore sempre é O(log N)?** Não. É O(h). Se balanceada, h é logarítmica; numa cadeia, a busca pode visitar todos os nós.

**A árvore é AVL?** Não. Apenas verificamos o critério de balanceamento e reconstruímos sob demanda. Não há rotações nas inserções ou remoções.

**BFS encontra a menor distância?** Encontra o menor número de arestas. Só coincide com a menor soma de pesos quando as condições dos pesos permitem, como todos iguais e positivos.

**DFS encontra o menor caminho?** Não há essa garantia; seu objetivo aqui é visita, alcançabilidade e apoio à detecção de ciclos.

**Por que marcar na entrada da fila do BFS?** Evita enfileirar o mesmo vértice repetidamente e fixa o primeiro predecessor na menor camada.

**Por que `PriorityQueue` pode ser usada?** É uma coleção auxiliar permitida. O algoritmo de Dijkstra continua implementado pelo projeto; a fila apenas organiza estados por prioridade.

**Por que há estados repetidos no Dijkstra?** Ao melhorar um custo, adicionamos um novo estado. Quando um estado antigo sai da fila com custo pior que o atual, é descartado.

**Qual a complexidade de BFS/DFS?** A travessia é O(V+E). Neste código, alguns métodos também ordenam os IDs de saída, acrescentando O(V log V). A lista de vizinhos já é mantida ordenada ao inserir.

**E a complexidade de Dijkstra?** Com fila que permite estados repetidos, O((V+E) log(V+E)) e espaço O(V+E), mais a preparação/saída. O projeto não usa heap com decrease-key.

**Uma aresta para um nó já visitado sempre indica ciclo?** Não. No grafo dirigido, é preciso que o destino esteja ativo na recursão; aresta para um nó já concluído pode existir em um DAG.

**Mão dupla conta como ciclo?** Nesta representação dirigida, sim: A → B → A. Para componentes, a direção é ignorada, mas essa é outra análise.

**Conectividade é forte?** Não. As componentes são da projeção não direcionada, ou conectividade fraca. O relatório deixa essa escolha explícita.

**Como garante que balancear não altera as conexões?** O grafo usa IDs estáveis. Reconstruir só troca a disposição dos nós da ABB, mantendo os IDs e os objetos das localidades.

**Como o sistema evita consumir memória com o vetor?** Calcula índices com `BigInteger` sem alocar lacunas e limita a representação real a 4.095 posições úteis no menu.

**Por que o vetor balanceado de oito nós tem 15 posições?** Balanceada não significa completa. A disposição escolhida pode ter lacunas; no exemplo um nó ocupa a posição 15.

**Os dados são salvos?** Não. O sistema recarrega a base fictícia a cada execução. Persistência não faz parte dos requisitos apresentados.

**Qual foi a contribuição de cada um?** Usar a divisão do relatório e descrever apenas as revisões, testes, mudanças e ensaios que realmente executaram. A primeira versão foi preparada com assistência de IA.
