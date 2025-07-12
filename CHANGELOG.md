# Changelog

Most notable changes to this project will be documented in this file

The format is loosely based on [Keep a Changelog](https://keepachangelog.com),
and this project adheres to [Semantic Versioning](https://semver.org)

## [Unreleased]

## 0.3.0

- Changed Strongbox behavior to tick opening on a cooldown (twice a second)
- Updated Strongbox sound effects
- New Maledictive Smithing Stones, which apply curses to items on upgrade
- New Fragility curse, which prevents items from being repaired

## 0.2.0

- Changed Bow critical hits to only apply when released on the last 1/5th of the pulling animation
- Changed the Tension upgrade to use the `max_pull_time_multiplier` attribute instead of just copying the Power enchantment
- Moved `max_durability` enchantment effect from Euclid's Elements to here
- Added new Strongbox texture (thanks obi)
- Added new (probably not that good) sounds for the Strongbox

## 0.1.2
Initial Beta Release!

- Added the Upgrade system
	- Removed all attribute-upgrading enchantments (Sharpness, Efficiency, etc.)
	- Added Smithing Stones, items which can be used to upgrade items when combined with specific stone types
	- Completely data-driven, using the same effect system as enchantments
- Removed the anvil repair cost penalty after repairing items
- Removed the Mending enchantment altogether
- Replaced Chests in ancient cities with Strongboxes, which take longer to open and break than regular chests
- Added new tooltips for enchantments / armor trims
