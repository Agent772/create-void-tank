## Unreleased

### Added
- Drill Drain compat: a moving contraption with a Void Tank on board voids the fluid its Drill Drains collect. That fluid is voided right away and never goes into fluid tanks on the contraption. The Void Tank must have been active when the contraption was assembled. Fluid from other sources is not affected. Without Drill Drain installed nothing changes.
- Server config option `voidDrillDrainFluid` (default `true`) to turn this off.

## Version 1.1.0

### Added
- Void Tank block that accepts and voids any fluid piped into it
- Crafting recipe using copper sheets, obsidian, and an ender pearl
- Configurable activation conditions: Always Active (default), Requires Heat, and Requires Redstone
- Window variant toggled by right-clicking with a wrench
- Create Goggles overlay showing voiding status, currently voided fluid, and unmet requirements
- Ponder scene demonstrating fluid voiding and all activation modes
- Jade mod integration displaying tank status
- Shift-expandable item tooltip with activation requirement details
- Loot table so the tank drops itself when mined
- Mineable with pickaxe tag

### Changed
- Updated Void Tank textures for improved appearance

### Fixed
- Fixed goggle overlay text overlapping with the status icon
- Fixed heat level not displaying correctly in the goggle overlay format string
