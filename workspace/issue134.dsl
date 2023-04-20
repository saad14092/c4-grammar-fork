workspace "Big workspace" {
  description "Vamos começar colocando tudo aqui"

  model {

    # properties {
    #   "structurizr.groupSeparator" "|"
    # }

    s1_ss = softwareSystem "S1" {
      c1_c = container "C1" {
        description "C1 desc"
      }
    }

    s2_ss = softwareSystem "S2" {
      c2_c = container "C2" {
        description "C2 desc"
      }
    }

    s3_ss = softwareSystem "S3" {
      c3_c = container "C3" {
        description "C3 desc"
      }
    }

    g1 = group "G1" {
      s4_ss = softwareSystem "S4" {
        c4_c = container "C4" {
          description "C4 desc"
        }
      }

      s5_ss = softwareSystem "S5" {
        c5_c = container "C5" {
          description "C5 desc"
        }
      }

      s6_ss = softwareSystem "S6" {
        c6_c = container "C6" {
          description "C6 desc"
        }
      }
    }

    g2 = group "G2" {
      s7_ss = softwareSystem "S7" {
        c7_c = container "C7" {
          description "C7 desc"
        }
      }

      s8_ss = softwareSystem "S8" {
        c8_c = container "C8" {
          description "C8 desc"
        }
      }

      s9_ss = softwareSystem "S9" {
        c9_c = container "C9" {
          description "C9 desc"
        }
      }
    }

    g3 = group "G3" {
      s10_ss = softwareSystem "S10" {
        c10_c = container "C10" {
          description "C10 desc"
        }
      }

      s11_ss = softwareSystem "S11" {
        c11_c = container "C11" {
          description "C11 desc"
        }
      }

      s12_ss = softwareSystem "S12" {
        c12_c = container "C12" {
          description "C12 desc"
        }
      }
    }

    c10_c -> c10_c
    // Here  ^

  }

  views {

    !script groovy {
       workspace.views.createDefaultViews()
       workspace.views.views.each { it.disableAutomaticLayout() }
     }

    theme default
  }
}
