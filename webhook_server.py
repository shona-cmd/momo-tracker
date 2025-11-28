from flask import Flask, request, jsonify
import hmac
import hashlib
import os
from datetime import datetime
from flask import send_file

app = Flask(__name__)

import json

GITHUB_WEBHOOK_SECRET = os.environ.get("SECRET_TOKEN", "your_github_webhook_secret")

@app.route('/payload', methods=['POST'])
def github_webhook():
    request.get_data()
    payload_body = request.data.decode('utf-8')
    
    signature = 'sha256=' + hmac.new(
        GITHUB_WEBHOOK_SECRET.encode('utf-8'),
        msg=request.data,
        digestmod=hashlib.sha256
    ).hexdigest()

    if not hmac.compare_digest(signature, request.headers.get('X-Hub-Signature-256', '')):
        return "Signatures didn't match!", 400

    try:
        push = json.loads(payload_body)
    except json.JSONDecodeError:
        return "Invalid JSON format", 400

    return "I got some JSON: {}".format(push), 200

# === CONFIG (CHANGE THESE) ===
MTN_SUBSCRIPTION_KEY = "your_mtn_primary_key_here"
MTN_SECRET = "your_mtn_webhook_secret_if_any"  # Some environments require it
PAID_PHONES_FILE = "paid_phones.txt"  # Auto-created
APK_PATH = "app-release.apk"  # Put your signed APK in same folder

# === MTN MoMo Callback ===
@app.route('/mtn-callback', methods=['POST'])
def mtn_callback():
    # MTN sends HMAC signature in header X-MTN-HMAC-SHA256
    signature = request.headers.get('X-MTN-HMAC-SHA256', '')
    payload = request.data

    # Verify signature (critical for security)
    expected = hmac.new(MTN_SUBSCRIPTION_KEY.encode(), payload, hashlib.sha256).hexdigest()
    if not hmac.compare_digest(signature, expected):
        return "Invalid signature", 400

    data = request.json
    status = data.get('status')
    external_id = data.get('externalId') or data.get('payer', {}).get('partyId', '')

    if status == "SUCCESSFUL":
        phone = data.get('payer', {}).get('partyId', '').replace('256', '0')
        with open(PAID_PHONES_FILE, 'a') as f:
            f.write(f"{phone}|MTN|{datetime.now().isoformat()}\n")
        print(f"MTN PAID: {phone}")
    return "OK", 200


# === Airtel Money Callback ===
@app.route('/airtel-callback', methods=['POST'])
def airtel_callback():
    data = request.json
    status = data.get('status', '').upper()
    phone = data.get('payer', {}).get('phone_number', '').replace('256', '0')

    if status == "SUCCESSFUL":
        with open(PAID_PHONES_FILE, 'a') as f:
            f.write(f"{phone}|Airtel|{datetime.now().isoformat()}\n")
        print(f"AIRTEL PAID: {phone}")
    return "OK", 200


# === Check if user has paid (your app calls this) ===
@app.route('/check-payment')
def check_payment():
    phone = request.args.get('phone', '').strip()
    if not phone.startswith('0'):
        phone = '0' + phone[-9:]  # normalize

    if os.path.exists(PAID_PHONES_FILE):
        with open(PAID_PHONES_FILE) as f:
            for line in f:
                if phone in line:
                    return jsonify({"paid": True, "message": "Pro Unlocked!"})
    
    return jsonify({"paid": False, "message": "Send 5000 UGX to 0745128746"})


# === Direct APK download (after payment) ===
@app.route('/download')
def download_apk():
    phone = request.args.get('phone', '').strip()
    if not phone.startswith('0'):
        phone = '0' + phone[-9:]

    if os.path.exists(PAID_PHONES_FILE):
        with open(PAID_PHONES_FILE) as f:
            if any(phone in line for line in f):
                return send_file(APK_PATH, as_attachment=True, 
                                download_name="Momo_Tracker_Pro_v1.0.apk")

    return "Payment not found. Send 5000 UGX first.", 402


@app.route('/')
def home():
    warning = ""
    if GITHUB_WEBHOOK_SECRET == "your_github_webhook_secret":
        warning = "<p style='color:red'>WARNING: Please set the GITHUB_WEBHOOK_SECRET environment variable!</p>"
    return f'''
    <h1>Momo Tracker Pro – Webhook Live</h1>
    {warning}
    <p>MTN URL: https://your-server.com/mtn-callback</p>
    <p>Airtel URL: https://your-server.com/airtel-callback</p>
    <p>Check payment: /check-payment?phone=075xxxxxxx</p>
    <p>Download: /download?phone=075xxxxxxx</p>
    '''

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
