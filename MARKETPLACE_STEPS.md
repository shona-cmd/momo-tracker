Step,Action,Details & Code/Tools,Timeline
1. Register as OAuth GitHub App,Create the app for Marketplace submission.,"- Go to https://github.com/settings/applications/new.
- Name: ""Momo Tracker"".
- Homepage: https://github.com/shona-cmd/momo-tracker.
- Description: ""Track GitHub issues + MoMo transactions on Android.""
- Callback: momo-tracker://oauth/callback.
- Scopes: repo (for issue tracking).
- Generate client ID/secret—store in app (e.g., local.properties).
This qualifies as ""integration beyond auth"" (API fetches + MoMo sync).",1 day
2. Update App for Marketplace Compliance,Add billing webhooks + MoMo flow.,"- In Android (app/build.gradle): Add Retrofit for GitHub API (from prior).
- Backend for MoMo/Webhooks: Use Node.js (free on Vercel). Create server.js:
```javascript:disable-run",
3. Set Pricing Plans,Define free/pro tiers.,"- In app settings > Pricing: Free (basic tracking, 5 repos), Pro ($4.99/mo or 5000 UGX one-time via MoMo).
- For MoMo: On purchase event, backend collects to your number, then provisions access (e.g., API key for premium).
- Supports monthly/annual; free encouraged for installs.",1 day
4. Submit for Listing,Request publication.,"- Update README/PRIVACY.md (from prior: add MoMo flow).
- Assets: Logo (prior squircle ""M"" design), screenshots (dashboard with MoMo sync).
- Submit at https://github.com/marketplace/new (as OAuth App).
- Requirements met: 100 installs (promote on Reddit/r/Uganda, X; start beta), verified org (transfer repo to new org, verify).
- Review: 1-2 weeks; free first, paid after.",1 week
5. Handle Payouts & Scaling,Receive funds.,"- Global: GitHub pays you ~70% revenue (after fees) to bank/PayPal.
- MoMo: Direct to 0745128746 via API collections—no GitHub cut.
- Track: Use MTN dashboard for txns; Firebase for user analytics.
- Uganda focus: Market on WhatsApp groups, Facebook (e.g., ""Pay 5000 UGX via MoMo for lifetime premium"").
- Revenue est: 100 installs @ 20% conversion = ~2M UGX/month initial.",Ongoing
