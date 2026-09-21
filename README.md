# TVTime

TVTime is a Google TV screen-time dashboard that shows real app usage data from Android.

## What it does

- Reads app usage from Android's UsageStatsManager API
- Lists how many minutes each app was used today
- Shows total screen time and top apps
- Designed for Google TV with large focusable controls
- Read-only tracking, not a parental-control blocker

## Android permission

This app requires:

```xml
android.permission.PACKAGE_USAGE_STATS
