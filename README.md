# eNotes Android SDK for LUKSO

## Requirements

- Android 5.1+
- Gradle 3.4.0

## Installation

api project(':core')

## Usage

### Get CardManager

CardManager cardManager = new CardManager(activity);

### API

- readBlockchainPublicKey
```
byte[] publicKey = mCardManager.readBlockchainPublicKey();

```

- readTransactionSignCounter
```
int signCount = mCardManager.readTransactionSignCounter();
```

- verifyBloackchainPublicKey (challenge-response)
```
boolean result = mCardManager.verifyBloackchainPublicKey(mPublicKey)
```

- signTransactionHash
```
CardManager.Pair pair = mCardManager.signTransactionHash(tx.getRawHash(), mPublicKey);
byte[] r = pair.getR();
byte[] s = pair.getS();
```

### Permissions

```
    <uses-feature
        android:name="android.hardware.nfc"
        android:required="false" />
    <uses-permission android:name="android.permission.NFC" />
```

## Example

There is an example application in ```/example``` folder, please check it for your information.

## Author

[eNotes.io](https://enotes.io)

## License

eNotesSDKLukso is available under the MIT license. See the LICENSE file for more info.
