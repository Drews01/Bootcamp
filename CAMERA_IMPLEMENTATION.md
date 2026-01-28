# Camera & Image Upload Implementation

This document outlines the technical implementation of the Camera capture and Image Upload feature in the application, specifically used for KTP (ID Card) uploads in the "Edit Profile" screen.

## Overview

The feature allows users to:
1.  Capture a photo using the device's native Camera app.
2.  Select an image from the Gallery.
3.  Upload the image securely to the server.
4.  Persist the capture state across process death (e.g., if the app is killed while the camera is open).

## Architecture & Components

### 1. FileProvider Configuration
To securely share file URIs with the Camera application, we use `androidx.core.content.FileProvider`.

-   **Manifest**: Defined in `AndroidManifest.xml` with authority `${applicationId}.fileprovider`.
-   **Paths**: Configured in `res/xml/file_paths.xml`.
    ```xml
    <paths>
        <!-- External cache path for camera images -->
        <cache-path name="internal_cache_images" path="/" />
    </paths>
    ```

### 2. UI Layer (`EditProfileScreen.kt`)
The UI handles the intent launching and temporary file creation.

-   **`tempCameraUri` Persistence**:
    We use `rememberSaveable` to store the *String* representation of the temporary URI. This ensures that if the OS kills the app process to free up memory for the Camera app, the URI is restored when the user returns, preventing a "null pointer" or missing image issue.
    ```kotlin
    var tempCameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    ```

-   **Temporary File Creation**:
    Files are created in `context.cacheDir`.
    **Important**: We explicitly do *not* use `deleteOnExit()` on the temp file builder, as this can cause the file to be deleted prematurely if the process is recreated, leading to "File not found" errors.

-   **Launchers**:
    -   `ActivityResultContracts.TakePicture()`: For Camera.
    -   `ActivityResultContracts.GetContent()`: For Gallery.

### 3. ViewModel Layer (`EditProfileViewModel.kt`)
Handles the business logic and upload process.

-   **Image Size Validation**: Checks file size (limit: 5MB) using `ContentResolver` to handle both `FileProvider` URIs and Content URIs uniformly.
-   **Upload Logic**:
    -   Calls `UserProfileRepository.uploadKtp(uri)`.
    -   **Null Path Handling**: If the server returns a successful response but the `ktpPath` field is null/empty, the ViewModel treats this as a success and triggers a `loadProfile()` to fetch the updated profile data from the server. This handles cases where the upload endpoint's response might be partial.

### 4. Data Layer (`UserProfileRepositoryImpl.kt` & `RemoteDataSource`)
-   **`uploadKtp`**:
    -   Reads the stream from the URI.
    -   Creates a temporary file copy for the multipart upload.
    -   Sends `MultipartBody.Part` with the request.
    -   Returns `Result<String>` (the path). Maps null/empty API responses to `Result.success("")` to allow the ViewModel to handle the reload logic.

### 5. Profile Refresh (`ProfileDetailsScreen.kt`)
To ensure the new photo is visible immediately after returning from the "Edit Profile" screen:
-   Uses `LifecycleEventObserver` on `ON_RESUME`.
-   Triggers `viewModel.loadProfile()` whenever the screen becomes active, ensuring fresh data is always displayed.

## Key Challenges & Solutions

| Challenge | Solution |
| :--- | :--- |
| **Process Death** | The app process may die while the Camera is open. We use `rememberSaveable` for the URI and removed `deleteOnExit()` to ensure the file and its reference survive the restart. |
| **Null API Response** | The server might upload successfully but return a null path. The app logic now detects this, treats it as success, and re-fetches the full profile to get the correct path. |
| **Stale Data** | `ProfileDetailsScreen` observes `ON_RESUME` to auto-refresh data when navigating back. |

## Permission Handling
The app requests `Manifest.permission.CAMERA` before launching the camera. if denied, it falls back gracefully (though currently just suppresses the action; a Snackbar prompt is recommended).
