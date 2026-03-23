const { app, BrowserWindow, shell, Menu, ipcMain } = require('electron');
const path = require('path');
const { exec } = require('child_process');

const isDev = !app.isPackaged;

// Register 'gitdense' as the default protocol client
if (process.defaultApp) {
  if (process.argv.length >= 2) {
    app.setAsDefaultProtocolClient('gitdense', process.execPath, [path.resolve(process.argv[1])]);
  }
} else {
  app.setAsDefaultProtocolClient('gitdense');
}

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 820,
    minWidth: 1024,
    minHeight: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      nodeIntegration: false,
      contextIsolation: true,
      sandbox: false,
      webSecurity: false,
    },
    titleBarStyle: 'hidden',
    titleBarOverlay: {
      color: '#09090b',
      symbolColor: '#a1a1aa',
      height: 32,
    },
    title: 'GitTEnz',
    backgroundColor: '#09090b',
    show: false,
  });

  // Protocol Handler for Deep Linking (token login)
  const handleProtocolUrl = (url) => {
    try {
      const parsedUrl = new URL(url);
      if (parsedUrl.protocol === 'gitdense:' && parsedUrl.host === 'auth') {
        const token = parsedUrl.searchParams.get('token');
        if (token) {
          mainWindow.webContents.send('auth-token-received', token);
          if (mainWindow.isMinimized()) mainWindow.restore();
          mainWindow.focus();
        }
      }
    } catch (e) {
      console.warn('Failed to parse protocol URL:', e.message);
    }
  };

  // Check if app was opened via protocol on startup
  const argUrl = process.argv.find(arg => arg.startsWith('gitdense://'));
  if (argUrl) {
    mainWindow.once('ready-to-show', () => handleProtocolUrl(argUrl));
  }

  // Handle subsequent protocol requests (app already running)
  app.on('second-instance', (event, commandLine) => {
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore();
      mainWindow.focus();
      
      const secondArgUrl = commandLine.find(arg => arg.startsWith('gitdense://'));
      if (secondArgUrl) handleProtocolUrl(secondArgUrl);
    }
  });

  // Hide the default menu bar
  Menu.setApplicationMenu(null);

  if (isDev) {
    mainWindow.loadURL('http://localhost:5175').catch((err) => { // Updated to port 5175
      console.error('Could not connect to Vite dev server:', err.message);
    });
    mainWindow.webContents.openDevTools({ mode: 'detach' });
  } else {
    mainWindow.loadFile(path.join(__dirname, 'dist', 'index.html')).catch((err) => {
      console.error('Could not load production build:', err.message);
    });
  }

  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
  });

  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    if (url.startsWith('https:') || url.startsWith('http:')) {
      shell.openExternal(url);
      return { action: 'deny' };
    }
    return { action: 'allow' };
  });

  mainWindow.webContents.on('will-navigate', (event, url) => {
    try {
      const parsedUrl = new URL(url);
      if (parsedUrl.hostname !== 'localhost' && parsedUrl.protocol !== 'file:') {
        event.preventDefault();
        shell.openExternal(url);
      }
    } catch {}
  });
}

// Ensure single instance lock for deep linking to work correctly
const gotTheLock = app.requestSingleInstanceLock();
if (!gotTheLock) {
  app.quit();
} else {
  app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
      if (BrowserWindow.getAllWindows().length === 0) {
        createWindow();
      }
    });
  });
}

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// IPC Git Command Handler
ipcMain.handle('run-git-command', async (event, { cwd, args }) => {
  return new Promise((resolve) => {
    const validArgs = args.filter(a => typeof a === 'string');
    const command = `git ${validArgs.join(' ')}`;
    
    exec(command, { cwd }, (error, stdout, stderr) => {
      resolve({
        success: !error,
        stdout,
        stderr,
        error: error ? error.message : null
      });
    });
  });
});
