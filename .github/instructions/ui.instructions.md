---
applyTo:
  - "**/ui/**"
  - "**/*Activity.kt"
---

# UI Code Instructions

This directory contains Android Activity and UI components for the forensic evidence application.

## Architecture Requirements

1. **Use ViewBinding** - Never use `findViewById()`, always use ViewBinding
2. **Coroutines for async** - Use Kotlin Coroutines for asynchronous operations
3. **Material Design** - Follow Material Design 3 guidelines
4. **Accessibility** - Ensure all UI elements are accessible

## Lifecycle Awareness

- Be mindful of Activity lifecycle states
- Clean up resources in `onDestroy()`
- Handle configuration changes appropriately
- Save and restore state in `onSaveInstanceState()`/`onRestoreInstanceState()`

## Permissions

- Always check permissions before using camera or location
- Handle permission denial gracefully with user-friendly messages
- Use the appropriate permission request APIs

## Offline-First

- Never assume network connectivity
- All UI operations must work offline
- No loading states that depend on network responses

## Coding Style

```kotlin
// ViewBinding initialization
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
}
```

## Testing

- Write instrumentation tests for UI components
- Test permission handling flows
- Test configuration changes (rotation, dark mode)
