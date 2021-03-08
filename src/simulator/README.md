# QMMSimulator

## Usage

```java
int Ndim = 100, Nsweep = 100;
Function<Double, Double> Vpot = (x) -> { return 0.5*x*x; };
QMMSimulator simulator = new QMMSimulator(1, Ndim, 1.0, 1.0, Vpot, XInitSettings.fixed(0));
for(int sweep = 1; sweep <= Nsweep; ++ sweep) {
    simulator.simulate();
    SweepData data = simulator.getSweepData();
    data.print();
    // data.printVerbose();
}
```
