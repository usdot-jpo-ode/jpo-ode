# Data Flow Diagrams
The purpose of these diagrams is to show:
- how data flows through the Operation Data Environment (both in an overall sense and for each message type)
- how the ODE interacts with kafka topics as well as its submodules.

## Key Explanation
- The blue rectangles are java classes that belong to this repository.
- The yellow ovals are kafka topics that the ODE and its submodules consume from and produce to.
- The red rectangles are groups of java classes.
- The red ovals are groups of kafka topics.
- The green rectangles are submodules of the ODE.
- The arrows indicate the data flow. The beginning of the arrow is where it flows from and the end of the arrow is where it flows to.

## List of Diagrams
- ODE Data Flow Overview
- BSM Data Flow
- TIM Data Flow
- SPAT Data Flow
- MAP Data Flow
- SRM Data Flow
- SSM Data Flow