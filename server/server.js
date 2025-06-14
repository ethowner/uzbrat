const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = 3000;
const DATA_FILE = path.join(__dirname, 'devices.json');
const ONLINE_TIMEOUT = 60000; // 1 минута

function readDevices() {
    try {
        const data = fs.readFileSync(DATA_FILE, 'utf8');
        return JSON.parse(data);
    } catch (err) {
        return {};
    }
}

function saveDevices(devices) {
    fs.writeFileSync(DATA_FILE, JSON.stringify(devices, null, 2));
}

function handleApi(req, res) {
    const parsedUrl = url.parse(req.url, true);
    if (req.method === 'POST' && parsedUrl.pathname === '/api/device') {
        let body = '';
        req.on('data', chunk => body += chunk);
        req.on('end', () => {
            try {
                const device = JSON.parse(body);
                const devices = readDevices();
                device.timestamp = Date.now();
                devices[device.id] = device;
                saveDevices(devices);
                res.writeHead(200, {'Content-Type': 'application/json'});
                res.end(JSON.stringify({status: 'ok'}));
            } catch (e) {
                res.writeHead(400, {'Content-Type': 'application/json'});
                res.end(JSON.stringify({error: 'invalid json'}));
            }
        });
    } else if (req.method === 'GET' && parsedUrl.pathname === '/api/devices') {
        const devices = readDevices();
        const list = Object.values(devices).map(d => ({
            ...d,
            online: Date.now() - d.timestamp < ONLINE_TIMEOUT
        }));
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify(list));
    } else {
        res.writeHead(404);
        res.end();
    }
}

function serveStatic(req, res) {
    let filePath = path.join(__dirname, 'public', req.url === '/' ? 'index.html' : req.url);
    fs.readFile(filePath, (err, content) => {
        if (err) {
            res.writeHead(404);
            res.end('Not found');
        } else {
            const ext = path.extname(filePath);
            const contentType = ext === '.css' ? 'text/css' : 'text/html';
            res.writeHead(200, {'Content-Type': contentType});
            res.end(content);
        }
    });
}

const server = http.createServer((req, res) => {
    if (req.url.startsWith('/api/')) {
        handleApi(req, res);
    } else {
        serveStatic(req, res);
    }
});

server.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
