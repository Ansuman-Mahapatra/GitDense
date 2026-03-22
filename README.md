# GitDense — Desktop Application for GitTEnz

[![Download](https://img.shields.io/badge/Download-Latest%20Installer-blue?style=for-the-badge)](https://github.com/Ansuman-Mahapatra/GitDense/raw/main/dist-electron/GitDense-Setup-1.0.0.exe)
[![Backend](https://img.shields.io/badge/API-gittenz.onrender.com-orange?style=for-the-badge)](https://gittenz.onrender.com/api/public/health)

GitDense is the native Electron desktop application for the [GitTEnz](https://gittenz.vercel.app) platform. It wraps the full React frontend into a cross-platform executable with a distinct **Light Blue** theme, persistent sessions, and seamless GitHub OAuth that redirects back to the desktop app.

---

## ✨ What Makes the Desktop App Different

| Feature | Web App | Desktop App (GitDense) |
|---|---|---|
| Theme | Matrix Green | Light Blue |
| Sessions | 30-day inactivity logout | **Persistent** — logged in until sign out |
| Landing Page | Full marketing homepage | Goes **directly to Login** |
| GitHub OAuth | Redirects to Vercel | Redirects **back to desktop app** |
| Access | Browser | Installed native `.exe` |
| Installer | N/A | **One-click silent overwrite** (auto-update) |

---

## 📥 Install (End User)

Download and run the latest installer:

👉 **[GitDense-Setup-1.0.0.exe](https://github.com/Ansuman-Mahapatra/GitDense/raw/main/dist-electron/GitDense-Setup-1.0.0.exe)**

- The installer is silent and one-click — no wizard dialogs
- If a previous version is installed, it **automatically overwrites** it with the new version
- After install, GitDense opens automatically

---

## 🛠️ Development Setup

### Prerequisites

- Node.js v18+
- npm

### 1. Clone the Repository

```bash
git clone https://github.com/Ansuman-Mahapatra/GitDense.git
cd GitDense
npm install
```

### 2. Configure Environment

The `.env` file controls which backend the desktop app connects to.

**Default (connects to cloud backend):**
```env
# .env — used in local development (web view at localhost:5175)
VITE_API_URL=https://gittenz.onrender.com
```

> 💡 The default points to the cloud backend so you can test with real accounts that exist in the cloud database. If you want to test against a local backend, change this to `http://localhost:8080`.

**Production builds (packaged `.exe`):**
```env
# .env.production — used automatically when running `npm run dist`
VITE_API_URL=https://gittenz.onrender.com
```

### 3. Run in Browser (Web View for Development)

```bash
npx vite --force --port 5175 --host 0.0.0.0
```

Open `http://localhost:5175` in your browser.

> This runs the GitDense UI in a normal browser tab. It connects to the cloud backend by default, so you can log in with your real account from `gittenz.vercel.app`.

### 4. Run as Electron App

```bash
npm run dev
```

This opens the app as a native Electron window.

---

## 📦 Building the Installer

```bash
npm run dist
```

This runs:
1. `vite build` — compiles the React app using `.env.production` (cloud backend)
2. `electron-builder` — packages it into a Windows NSIS installer

**Output:** `dist-electron/GitDense-Setup-1.0.0.exe`

To release a new version, update the `"version"` field in `package.json` before running `npm run dist`.

---

## 🔑 Session & Authentication

### Persistent Login
Once you log in to GitDense, your session token is saved in `localStorage`. The app will automatically log you back in every time it opens — **forever** until you manually click "Sign Out".

- JWT tokens from the backend are valid for **10 years**
- No inactivity timers — desktop apps should remember you like Spotify or Slack
- The `lastActivityTimestamp` is still updated silently to keep the backend's GitHub verification rolling window alive

### GitHub OAuth Smart Redirect
When you click **"Verify with GitHub"**, the desktop app:
1. Passes its own origin (`http://localhost:5175` or `file://...`) as a `redirect_origin` in the OAuth state
2. GitHub redirects to the backend callback
3. The backend reads the `redirect_origin` and sends the token **back to the desktop app**, not to `gittenz.vercel.app`

This means GitHub verification completes inside the desktop app itself.

---

## 🎨 Theme

GitDense uses a completely separate `index.css` with a **Light Blue** color palette (`hsl(200, 90%, ...)`) rather than the website's green theme. This differentiates the desktop experience at a glance.

---

## 🏗️ Project Structure

```
GitDense/
├── main.js                 # Electron main process
├── preload.js              # Electron preload script
├── src/
│   ├── App.tsx             # Routes: / → /login (no marketing pages)
│   ├── index.css           # Light blue theme design system
│   ├── config.ts           # API URL from env vars
│   ├── lib/
│   │   └── auth.tsx        # Persistent session, smart GitHub OAuth
│   ├── pages/
│   │   ├── LoginPage.tsx
│   │   ├── SignupPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── RepositoryDetailPage.tsx
│   │   └── AuthSuccessPage.tsx
│   └── components/
│       ├── layout/
│       │   ├── Sidebar.tsx           # Light blue nav + Deleted Repos tab
│       │   └── ServerWakeUp.tsx      # Backend health check overlay
│       └── auth/
│           └── GitHubVerificationOverlay.tsx
├── .env                    # Dev: cloud backend URL
├── .env.production         # Prod: cloud backend URL
├── package.json            # electron-builder config (oneClick: true)
└── vite.config.ts          # Port 5175 for dev web view
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---|---|
| "User not found" on login | Your account must exist on the cloud database. Try signing in at [gittenz.vercel.app](https://gittenz.vercel.app) first to confirm |
| GitHub OAuth redirects to wrong app | Ensure backend (`OAuth2LoginSuccessHandler.java`) is redeployed after changes |
| Images/logo not showing in `.exe` | All asset paths use relative `./` paths — do not change to `/` |
| App opens to marketing homepage | Update `App.tsx` — root route must redirect to `/login` |
| Old version still installed | Run the new installer — `oneClick: true` auto-overwrites silently |

---

## 📄 License

MIT License

---

## 👤 Author

**Ansuman Mahapatra**

- 🌐 Web App: [gittenz.vercel.app](https://gittenz.vercel.app)
- 🐙 GitHub: [@Ansuman-Mahapatra](https://github.com/Ansuman-Mahapatra)
- 📧 Email: ansuman197463@gmail.com
