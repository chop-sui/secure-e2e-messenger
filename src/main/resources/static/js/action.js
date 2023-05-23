const socket = new SockJS('/secured/chat');
const stompClient = Stomp.over(socket);
let sessionId = "";
let sharedSecretKey = "";

async function sendMessage() {
    if (!receiver) {
        alert("Please select a friend to chat with");
        return;
    }

    const messageInput = document.getElementById('message');

    const encodedMsg = new TextEncoder().encode(messageInput.value);
    const encryptedMsg = await crypto.subtle.encrypt(
        {name: "AES-GCM", iv: new TextEncoder().encode("Initialization Vector")},
        sharedSecretKey,
        encodedMsg
    );

    const uintArray = new Uint8Array(encryptedMsg);
    const str = String.fromCharCode.apply(null, uintArray);
    const base64Msg = btoa(str);

    let msg = {
        'from': username,
        'to': receiver,
        'text': base64Msg
    };

    stompClient.send("/app/secured/chat", {}, JSON.stringify(msg));
    showMessage({from: username, text: messageInput.value});
    messageInput.value = '';

}

function showMessage(message) {
    const messages = document.getElementById('messages');
    const li = document.createElement('li');
    li.appendChild(document.createTextNode(`${message.from}: ${message.text}`));
    messages.appendChild(li);
}

async function main() {
    const keyPair = await crypto.subtle.generateKey({
        name: 'ECDH',
        namedCurve: 'P-256'
    }, false, ["deriveKey", "deriveBits"]);

    const publicKeyJwk = await crypto.subtle.exportKey(
        "jwk",
        keyPair.publicKey
    );

    // const publicKeyBase64 = btoa(String.fromCharCode.apply(null, new Uint8Array(await crypto.subtle.exportKey('raw', publicKey))));
    // console.log(`Created publicKeyBase64: ${publicKeyBase64}`);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        let url = stompClient.ws._transport.url;

        url = url.replace(
            "ws://localhost:8080/secured/chat/", "");
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);
        sessionId = url;

        let msg = {
            'from': username,
            'to': receiver,
            'key': publicKeyJwk
        };
        stompClient.send('/app/secured/chat/key', {}, JSON.stringify(msg));

        stompClient.subscribe('/secured/user/queue/specific-user' + '-user' + sessionId, async function (message) {
            const data = JSON.parse(message.body);
            if (data['key']) {
                if (cnt === "1") {
                    let msg = {
                        'from': username,
                        'to': receiver,
                        'key': publicKeyJwk
                    };
                    stompClient.send('/app/secured/chat/key', {}, JSON.stringify(msg));
                }

                const friendPublicKeyJwk = data['key'];
                const publicKey = await crypto.subtle.importKey(
                    "jwk",
                    friendPublicKeyJwk,
                    {
                        name: "ECDH",
                        namedCurve: "P-256",
                    }, true, []
                );

                sharedSecretKey = await crypto.subtle.deriveKey(
                    {name: "ECDH", public: publicKey},
                    keyPair.privateKey,
                    {name: "AES-GCM", length: 256},
                    true,
                    ["encrypt", "decrypt"]
                );
            } else {
                try {
                    const msg = JSON.parse(message.body);
                    const text = msg.text;

                    const string = atob(text);
                    const uintArray = new Uint8Array(
                        [...string].map((char) => char.charCodeAt(0))
                    );
                    const algorithm = {
                        name: "AES-GCM",
                        iv: new TextEncoder().encode("Initialization Vector"),
                    };
                    const decryptedData = await window.crypto.subtle.decrypt(
                        algorithm,
                        sharedSecretKey,
                        uintArray
                    );

                    const decryptedMsg = new TextDecoder().decode(decryptedData);
                    showMessage({from: msg.from, text: decryptedMsg});
                } catch (e) {
                    console.log("Error decrypting message");
                    console.log(e.message);
                }
            }
        });
    });
}

main().then(r => console.log(r));

