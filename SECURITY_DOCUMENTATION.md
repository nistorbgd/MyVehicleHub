# MyVehicleHub Security & Authentication System

## 🎯 Overview

This document explains how users can securely access the MyVehicleHub application. Think of it like a digital key system for your app - some areas are public (anyone can enter), while others require proof of identity.

---

## 🔑 How Users Access the App

### **Step 1: Creating an Account (Registration)**

Just like signing up for any online service, users can create an account in two ways:

#### **Option A: Email & Password**
- User provides: email, password, name, age
- System creates account
- User can now login

#### **Option B: "Sign in with Google"**
- User clicks Google button
- Google verifies their identity
- System automatically creates account
- Faster, no password needed

**Endpoint:** `POST /api/v1/auth/register` (Email) or `POST /api/v1/auth/google` (Google)

---

### **Step 2: Logging In**

When users want to use the app, they login and receive two special "keys":

#### **Access Token (Short-term key)**
- **What it is:** A digital pass that proves "you are who you say you are"
- **How long it lasts:** 30 minutes
- **What it's for:** Every request to the app shows this pass
- **Think of it like:** A cinema ticket that expires after the movie

#### **Refresh Token (Long-term key)**
- **What it is:** A backup key to get new access tokens
- **How long it lasts:** 90 days
- **What it's for:** Getting new access tokens without re-entering password
- **Think of it like:** A season pass that lets you get daily tickets

**Endpoint:** `POST /api/v1/auth/login` (Email) or `POST /api/v1/auth/google` (Google)

**What user receives:**
```json
{
  "jwtToken": "eyJhbGc...",      ← Access token (30 min)
  "refreshToken": "550e8400..."  ← Refresh token (90 days)
}
```

---

### **Step 3: Using the App**

Every time the mobile app makes a request (view profile, add vehicle, etc.), it shows the **access token** to prove identity.

**Example:** Like showing your ID at airport security before boarding.

**How it works:**
```
Mobile App → "Here's my access token" → Server checks → "Valid! Here's your data"
```

---

### **Step 4: When Access Token Expires (Every 30 Minutes)**

The access token expires every 30 minutes for security. But users don't need to re-login!

**Automatic Process:**
```
1. Mobile app detects: "Access token expired"
2. Sends refresh token to server
3. Server gives new access token
4. User continues using app seamlessly
```

**User experience:** They never notice! The app handles it automatically.

**Endpoint:** `POST /api/v1/auth/refreshToken`

---

### **Step 5: Logging Out**

Users can logout in two ways:

#### **Option A: Logout from Current Device**
- Logs out from phone/tablet they're using
- Other devices stay logged in

#### **Option B: Logout from All Devices**
- Logs out everywhere (phone, tablet, computer)
- Useful if phone is lost/stolen

**Endpoints:** 
- `POST /api/v1/auth/logout` (current device)
- `POST /api/v1/auth/logout-all` (all devices)

---

## 👥 User Roles & Permissions

Not all users have the same privileges. There are three levels:

### **🙋 USER (Regular User)**
- **Who:** Default for all new accounts
- **Can do:** 
  - View and manage their own vehicles
  - Update their profile
  - Access all user features
- **Cannot do:**
  - Access admin areas
  - Manage other users

### **👑 ADMIN (Administrator)**
- **Who:** Promoted manually
- **Can do:**
  - Everything
  - Manage all users
  - Access admin dashboard
  - Change user roles

**Think of it like:** 
- USER = Regular customer in a store
- ADMIN = Store manager

---

## 🏷️ How We Track Registration Method

The system remembers how each user registered:

| Provider | How They Signed Up |
|----------|-------------------|
| **EMAIL** | Registered with email & password |
| **GOOGLE** | Used "Sign in with Google" |
| **FACEBOOK** | Used Facebook login (future) |
| **APPLE** | Used Apple Sign-In (future) |

**Why this matters:**
- Google users can't change password
- Email users need password reset option
- Analytics: Track which signup method is most popular

---

## 🔒 Security Features

### **1. Passwords Are Encrypted**
- Passwords never stored in plain text
- Uses BCrypt encryption (industry standard)
- Even admins can't see user passwords

### **2. JWT Tokens Are Signed**
- Each token has a digital signature
- Cannot be faked or tampered with
- Server verifies every token

### **3. Token Rotation**
- Refresh tokens change after each use
- Old tokens immediately invalidated
- Prevents replay attacks

### **4. Automatic Cleanup**
- Expired tokens deleted daily (midnight)
- Database stays clean
- No stale data

### **5. Session Isolation**
- Each device has separate refresh token
- Logout on one device doesn't affect others
- Emergency: "Logout all devices" available

---

## 📱 Mobile App Flow (From User's Perspective)

### **First Time User:**
```
1. Opens app
2. Taps "Sign in with Google" or "Register with Email"
3. Creates account (takes 10 seconds)
4. Automatically logged in
5. Starts using app
```

### **Returning User:**
```
1. Opens app
2. Still logged in! (refresh token valid)
3. Continues where they left off
```

### **After 30 Minutes:**
```
1. Using app normally
2. Access token expires (user doesn't notice)
3. App automatically gets new token
4. User continues seamlessly
```

### **After 90 Days of Not Using App:**
```
1. Opens app after 3 months
2. Refresh token expired
3. Needs to login again
4. Quick re-login, back to app
```

---

## 🚨 Error Messages (What Users See)

### **"Unauthorized" (401)**
**What it means:** "We don't know who you are"

**Common causes:**
- Not logged in
- Token expired after 90 days
- Logged out manually

**Solution:** Login again

---

### **"Forbidden" (403)**
**What it means:** "We know who you are, but you can't do this"

**Common causes:**
- Regular user trying to access admin area
- Insufficient permissions

**Solution:** Contact admin for access, or this feature isn't for your account type

---

### **"Bad Request" (400)**
**What it means:** "Something's wrong with what you sent"

**Common causes:**
- Missing information (email or password not provided)
- Invalid format (email doesn't look like an email)

**Solution:** Check the information and try again

---

## 🎯 Key Benefits for Users

| Feature | User Benefit |
|---------|-------------|
| **Refresh Tokens** | Stay logged in for 90 days, no constant re-login |
| **Token Rotation** | Extra security without extra hassle |
| **Google Sign-In** | Quick login, no password to remember |
| **Multi-device** | Use app on phone, tablet, computer simultaneously |
| **Logout All** | Lost phone? One click logs out everywhere |
| **Role System** | Right people have right access |
| **Auto-refresh** | Seamless experience, no interruptions |

---

## 📊 Token Lifecycle (Visual)

```
DAY 0: User logs in
├─ Gets: Access Token (30 min) + Refresh Token (90 days)
│
DAY 0 + 30 min: Access token expires
├─ App automatically uses refresh token
├─ Gets: New Access Token (30 min) + New Refresh Token (90 days)
│
DAY 1, 2, 3... Same process
├─ Every 30 min: automatic refresh
├─ User never notices
│
DAY 90: User hasn't used app in 90 days
├─ Refresh token expires
└─ User must login again (expected security measure)
```

---

## 🔐 Data We Store

### **User Information:**
- Email address
- Encrypted password (if registered with email)
- First name, last name, age
- Role (USER, ADMIN, MODERATOR)
- Registration method (EMAIL, GOOGLE, etc.)

### **Security Information:**
- Refresh tokens (encrypted)
- Token creation date
- Token expiration date
- Not stored: Access tokens (short-lived, no need)

### **What We DON'T Store:**
- ❌ Plain text passwords
- ❌ Google passwords
- ❌ Access tokens
- ❌ Social security numbers
- ❌ Credit card information

---

## 🎓 Technical Terms Simplified

| Technical Term | Plain English |
|---------------|---------------|
| **JWT (JSON Web Token)** | Digital ID card |
| **Authentication** | Proving who you are |
| **Authorization** | Checking what you're allowed to do |
| **Bearer Token** | The "key" you carry to access things |
| **Encryption** | Scrambling data so only authorized people can read it |
| **API Endpoint** | A specific address where app sends requests |
| **OAuth2** | Technology that lets you "Sign in with Google/Facebook" |
| **Stateless** | Server doesn't remember you between requests (checks your token each time) |

---

## ✅ Security Checklist

Our system ensures:
- ✅ Passwords encrypted (BCrypt)
- ✅ Tokens cannot be faked (signed with secret key)
- ✅ Tokens expire (30 min for access, 90 days for refresh)
- ✅ Old tokens invalidated (rotation)
- ✅ Users can logout remotely (all devices)
- ✅ Different permission levels (roles)
- ✅ Track how users registered (provider)
- ✅ Automatic cleanup (expired tokens deleted)
- ✅ Clear error messages (users know what went wrong)
- ✅ HTTPS ready (encrypted communication in production)

---

## 🎯 Summary (TL;DR)

1. **Users register** with email or Google
2. **Users login** and get two tokens (short-term and long-term)
3. **Users use the app** by showing their access token
4. **Token auto-refreshes** every 30 minutes (seamless)
5. **Users stay logged in** for 90 days
6. **Users can logout** from one or all devices
7. **Different users have different permissions** (USER, MODERATOR, ADMIN)
8. **System is secure** with encryption, token rotation, and proper error handling

---

**Bottom Line:** Users get a secure, seamless experience with minimal friction. The app handles all the complex security stuff automatically in the background! 🎉

