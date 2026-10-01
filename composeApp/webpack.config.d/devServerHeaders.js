// Compose Multiplatform Wasm needs cross-origin isolation so Skiko can use
// SharedArrayBuffer. Without these headers the browser never creates the canvas.
config.devServer = {
    ...(config.devServer || {}),
    headers: {
        ...((config.devServer && config.devServer.headers) || {}),
        "Cross-Origin-Opener-Policy": "same-origin",
        "Cross-Origin-Embedder-Policy": "require-corp",
    },
};

// Ktor's browser logger reads process.env. Node's process global is absent in Chrome.
const webpack = require("webpack");
config.plugins.push(
    new webpack.BannerPlugin({
        banner: "globalThis.process = globalThis.process || { env: {}, release: { name: 'browser' }, version: '', versions: {} };",
        raw: true,
        entryOnly: true,
    }),
);
