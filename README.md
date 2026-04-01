# Shorten - APP

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange)
![Hilt](https://img.shields.io/badge/Hilt-DI-red)
![Retrofit](https://img.shields.io/badge/Retrofit-HTTP-green)

App Android para encurtamento de URLs com autenticação, histórico de links, perfil de usuário e suporte a Google OAuth.

> 🔭 [Visualizar arquitetura interativa](https://guibsantos.github.io/Shorten-APP/)
> 🔗 Backend: [Shorten Backend](https://github.com/GuiBSantos/Shorten-backend)

---

## ✨ Funcionalidades

- 🔗 Encurtamento de URLs com slug personalizado
- 👤 Login com e-mail/senha e Google OAuth
- 📋 Histórico de URLs encurtadas
- 🖼️ Perfil com foto de usuário
- 🌙 Suporte a tema claro e escuro
- 🔒 Autenticação via JWT com refresh automático

---

## 🛠️ Tech Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Arquitetura | MVVM |
| Injeção de dependência | Hilt |
| HTTP | Retrofit + OkHttp |
| Auth | JWT + Google OAuth |
| Tema | Material 3 + ThemeManager |

---

## 📁 Estrutura

```
com.guibsantos.shortenapp/
├── data/
│   ├── local/          # ThemeManager, TokenManager
│   ├── model/          # AuthModels, ShortenUrlModels, User, HistoricoItem
│   ├── network/        # ApiService, RetrofitClient, AuthInterceptor
│   └── repository/     # AuthRepository, UrlRepository
├── di/
│   └── AppModule       # Hilt module
├── ui/
│   ├── components/     # GlassBox, GlassCard, LogoTopBar
│   │   └── backgrounds/# ModernBackground, OrbitalBackground
│   ├── screens/
│   │   ├── home/       # EncurtadorScreen + ShortenerViewModel
│   │   ├── login/      # LoginScreen + LoginViewModel
│   │   ├── profile/    # ProfileScreen + ProfileViewModel
│   │   ├── register/   # RegisterScreen + RegisterViewModel
│   │   └── splash/     # SplashScreen
│   ├── theme/          # Color, Theme, Type
│   └── utils/          # ErrorUtils
├── AppNavigation       # Navegação entre telas
├── MainActivity
└── ShorterApplication
```

---

## 🚀 Rodando localmente

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17+
- Emulador ou dispositivo físico (Android 8+)

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/GuiBSantos/Shorten-APP.git
```

2. Abra no Android Studio

3. Configure o `google-services.json` com suas credenciais do Firebase/Google OAuth

4. Aponte a base URL da API em `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/" // emulador
// ou
private const val BASE_URL = "http://SEU_IP:8080/"   // dispositivo físico
```

5. Run ▶️

---

## 🔗 Relacionado

- [Shorten Backend](https://github.com/GuiBSantos/Shorten-backend) — API REST em Java 21 + Spring Boot 3
