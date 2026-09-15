# Map Editor

A feature-rich 2D map editor developed to support the creation of large maps for a top-down RPG.

## Overview

This project began as a solution to a simple problem: while developing a top-down 2D RPG, I needed a practical way to create and edit the large grid-based maps used by the game. Initially, maps were being created manually using Notepad, which quickly became inefficient and difficult to manage.

I decided to build a graphical map editor to streamline the process.

What began as a relatively simple utility evolved into a substantial software engineering project involving multiple interacting systems, custom data structures, design patterns, and user-focused quality-of-life features.

The current application provides a powerful and flexible environment for creating and editing complex 2D maps while significantly reducing the manual effort involved in map creation.

## Features

### Map Editing

* Interactive graphical map editing
* Grid-based map construction
* Multiple editing tools for efficient map creation
* Support for large maps suitable for a 2D RPG environment

### Editing Tools

* **Brush Tool** — Paint tiles onto the map using customizable brush sizes and shapes.
* **Fill Bucket** — Quickly fill connected regions with a selected tile.
* **Magic Wand** — Select connected or matching regions of the map for efficient editing.
* **Brush Size & Shape Editor** — Customize the size and shape of the editing brush to support different map-building workflows.

### Layering System

The editor uses a dedicated layer system inspired by applications such as Paint.NET.

Maps can be organized into separate layers, allowing different elements of the environment to be edited independently. This makes it possible to construct complex scenes while maintaining separation between different types of map content.

### Undo & Redo

The editor implements an undo/redo system to allow users to safely experiment with their maps and revert unwanted changes.

The system is built around a command-based architecture, allowing editing operations to be represented as commands that can be executed, undone, and redone.

## Architecture & Design

The project evolved into a collection of interconnected systems designed to keep the editor manageable as its functionality expanded.

### Design Patterns

Several software design patterns are used throughout the project, including:

* **Command Pattern** — Used to represent editing operations and support the undo/redo system.
* **Event Bus** — Provides decoupled communication between different components of the application.
* **Singleton Pattern** — Used where centralized access to shared application systems is appropriate.

### Data Structures

The application also makes use of several fundamental data structures and algorithms, including:

* Event queues for managing application events
* Stacks for undo/redo functionality
* Collections for managing map and editor state
* Command histories for reversible editing operations

## Engineering Challenges

One of the most significant aspects of the project was managing the increasing complexity of the application as new features were introduced.

Features such as multiple editing tools, layers, selection systems, undo/redo functionality, and event-driven communication all need to interact without tightly coupling the entire application together.

This required careful consideration of:

* Separation of responsibilities
* Communication between independent systems
* State management
* Reversible operations
* User interaction and event handling
* Extensibility of editing tools
* Maintaining manageable code as the project grew

The project provided practical experience designing software beyond isolated classes or small assignments, particularly in understanding how larger systems can be decomposed into interacting components.

## Technologies

* **Java**
* Object-Oriented Programming
* Design Patterns
* Data Structures
* Event-Driven Architecture
* Git / GitHub

## Motivation

The Map Editor was created as a practical development tool for another project rather than as a standalone academic exercise.

The original goal was simply to replace manually editing map grids in Notepad.

As the editor grew, it became an opportunity to explore software architecture, design patterns, data structures, user-interface design, and the challenges involved in building a larger application from scratch.

## Project Status

The editor is currently a functional and feature-rich project, with the core systems working together to provide an efficient workflow for creating and editing 2D RPG maps.

The project also served as an important foundation for exploring more advanced software engineering concepts through a practical application.
