# Defaults
element "Element" {
    fontSize 32
    metadata false
    width 500
    height 340
    shape RoundedBox
}

relationship "Relationship" {
    dashed false
    routing Curved
    thickness 3
    fontSize 32
}

element "database" {
    shape Cylinder
}

element "messaging" {
    shape Pipe
}
relationship "async" {
    dashed true
}

