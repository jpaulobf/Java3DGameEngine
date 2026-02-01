# Java 3D Game Engine

Uma engine de jogo 3D simples desenvolvida em Java puro, utilizando renderização via software (sem bibliotecas gráficas externas como OpenGL para o pipeline principal).

O projeto contém dois exemplos de demonstração:

1.  **Space Shooter** (`Window.java`): Um jogo de nave espacial.
2.  **Racing Demo** (`WindowRace.java`): Um jogo de corrida infinita.

## 1. Space Shooter (Nave)

Classe principal: `com.java3d.engine.window.Window`

### Controles
*   **W / S**: Mover a nave verticalmente (Eixo Y).
*   **A / D**: Mover a nave lateralmente (Eixo X).
*   **Espaço**: Acelerar (Boost de velocidade com efeito visual de rastro nas estrelas e tremor de câmera).
*   **J**: Atirar lasers.
*   **F**: Alternar entre iluminação Flat (facetada) e Gouraud (suave).

### Mecânicas
*   **Navegação**: A nave se desloca automaticamente para frente no eixo Z.
*   **Ambiente**: Campo de estrelas infinito gerado proceduralmente.

## 2. Racing Demo (Carro)

Classe principal: `com.java3d.engine.window.WindowRace`

### Controles
*   **W / Seta Cima**: Acelerar.
*   **A / Seta Esquerda**: Virar para a esquerda.
*   **D / Seta Direita**: Virar para a direita.

## Funcionalidades da Engine

*   **Renderização**: Pipeline 3D customizado com suporte a Z-Buffer, Clipping, Iluminação Dinâmica e Rasterização de Polígonos.
*   **Câmera**: Sistema de câmera em terceira pessoa com suavização e efeitos.