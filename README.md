# RESERVE - Food Rescue App

RESERVE connects food donors with NGOs and community organizations to reduce food waste and feed those in need.

## Features

### For Donors
- **Add Donations** — Post surplus food with photos, quantity, pickup location, and expiry time
- **Track Donations** — View all donations with status filters (Available, Requested, Approved, Collected, Completed)
- **Manage Requests** — Approve or reject incoming NGO requests; competing requests auto-rejected on approval
- **Dashboard** — Stats overview with total, active, and completed donation counts

### For NGOs
- **Browse Food** — Search and filter available donations by food type
- **Request Food** — One-tap request with duplicate-request prevention
- **Track Requests** — View all requests with status filters and real-time donation status sync
- **Dashboard** — Available food count, request stats, and quick-action cards

### Shared
- **Authentication** — Email/password registration with role selection (Donor or NGO), login, password reset
- **Profile Management** — View and edit name, phone, address
- **Real-time Updates** — Firestore snapshot listeners keep all screens in sync

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose with type-safe `@Serializable` routes |
| State | ViewModel + StateFlow |
| Auth | Firebase Authentication |
| Database | Cloud Firestore (real-time listeners, batch writes, transactions) |
| Storage | Firebase Storage (food images) |
| Images | Coil |
| Build | Gradle with version catalogs |

## Architecture

```
com.example.reserve/
  data/
    model/          # Donation, FoodRequest, User, enums
    repository/     # DonationRepository, FoodRequestRepository,
                    # UserRepository, StorageRepository
  ui/
    components/     # ReserveCard, ReserveButton, ReserveTextField,
                    # StatusBadge, DetailRow, StateViews
    navigation/     # ReserveRoutes, ReserveNavGraph
    screens/
      auth/         # Login, Register, ForgotPassword, AuthViewModel
      splash/       # Animated splash screen
      donor/        # Home, AddDonation, MyDonations, DonationDetail
      ngo/          # Home, BrowseFood, FoodDetail, MyRequests, RequestDetail
      profile/      # Profile, EditProfile
    theme/          # Color, Type, Shape, Theme (light + dark)
```

## Firebase Security Rules

- **Users** — Read/write own document only; userId must match auth UID on create
- **Donations** — Authenticated read; create requires donorId == auth UID; owner can update any field; non-owners can only change status on valid transitions (AVAILABLE->REQUESTED, REQUESTED->AVAILABLE)
- **Food Requests** — Owner (NGO or donor) can read individual requests; authenticated list; create requires ngoId == auth UID; owner can update

## Required Firestore Composite Indexes

| Collection | Fields | Order |
|-----------|--------|-------|
| foodRequests | ngoId, requestTime | ASC, DESC |
| foodRequests | donationId, requestTime | ASC, DESC |
| donations | status, createdAt | ASC, DESC |
| donations | donorId, createdAt | ASC, DESC |

## Build

Prerequisites: Android Studio with SDK 37, JDK 17+

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Configuration

1. Create a Firebase project
2. Add an Android app with package name `com.example.reserve`
3. Download `google-services.json` to `app/`
4. Enable Email/Password authentication
5. Create Firestore database and deploy the security rules from `firestore.rules`
6. Create the composite indexes listed above
7. Enable Firebase Storage

## Team

- **Krishhma** — Donor features, authentication, Firebase integration
- **Srishtee** — NGO features, food browsing, request management
- **Dhvani** — Cross-feature sync, error handling, UI polish

## License

This project was developed as an academic project.
